package com.mind.luckyworld.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mind.luckyworld.R
import com.mind.luckyworld.databinding.RegisterHomeFragmentBinding
import com.mind.luckyworld.utils.InjectorUtils
import com.mind.luckyworld.utils.getWifiMacAddress
import com.mind.luckyworld.utils.isNetworkAvailable
import com.mind.luckyworld.viewmodel.RegisterHomeViewModel


class RegisterHomeFragment : Fragment() {

    private val viewModel: RegisterHomeViewModel by viewModels {
        InjectorUtils.provideRegisterHomeViewModelFactory(requireContext())
    }

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var macAddress: String

    private lateinit var binding: RegisterHomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterHomeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@RegisterHomeFragment
        initObserver()
        macAddress = getWifiMacAddress()
        return binding.root
    }

    private fun initObserver() {
        viewModel.isRegisterClick.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                if (isNetworkAvailable(requireContext()).not()) {
                    Snackbar.make(
                        requireView(),
                        "Please check your internet connection and try again!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@Observer
                } else {
                    checkUserRegister()
                }
            }
        })
    }

    private fun checkUserRegister() {
        binding.progressBar.visibility = View.VISIBLE
        val macAddress = getWifiMacAddress()
        val databaseReference = firebaseFirestore.collection("users")
        databaseReference.get().addOnSuccessListener { documentSnapShot ->
            if (!documentSnapShot.isEmpty) {
                documentSnapShot.forEach {
                    if (it != null && it.getString("deviceMac").equals(
                            macAddress,
                            true
                        )
                    ) {
                        Snackbar.make(
                            requireView(),
                            "You are already registered with us!",
                            Snackbar.LENGTH_LONG
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        return@addOnSuccessListener
                    }
                }
                if (view?.findNavController()?.currentDestination?.id == R.id.registerHomeFragment) {
                    binding.progressBar.visibility = View.GONE
                    view?.findNavController()
                        ?.navigate(R.id.action_registerHomeFragment_to_registerFragment)
                    viewModel.setClickToFalse()
                }
            } else {
                if (view?.findNavController()?.currentDestination?.id == R.id.registerHomeFragment) {
                    binding.progressBar.visibility = View.GONE
                    view?.findNavController()
                        ?.navigate(R.id.action_registerHomeFragment_to_registerFragment)
                    viewModel.setClickToFalse()
                }
            }
        }.addOnFailureListener {
            Snackbar.make(
                requireView(),
                "Something went wrong please try again!",
                Snackbar.LENGTH_LONG
            ).show()
            binding.progressBar.visibility = View.GONE
        }
    }
}
