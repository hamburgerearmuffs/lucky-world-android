package com.mind.luckyworld.view.fragment

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.transition.Transition
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.CircularPropagation
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.mind.luckyworld.databinding.RegisterSuccessFragmentBinding
import com.mind.luckyworld.utils.InjectorUtils
import com.mind.luckyworld.viewmodel.RegisterSuccessViewModel

class RegisterSuccessFragment : Fragment() {

    private val viewModel: RegisterSuccessViewModel by viewModels {
        InjectorUtils.provideRegisterSuccessViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = RegisterSuccessFragmentBinding.inflate(inflater, container, false)
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        viewModel.eventBuzz.observe(this, Observer { buzz ->
            buzz(buzz)
//            viewModel.onBuzzComplete()
        })

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.timerData == 0) {
                viewModel.startTimer()
                Snackbar.make(
                    requireView(),
                    "Press back again to exit!",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
