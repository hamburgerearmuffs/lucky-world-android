package com.mind.luckyworld.viewmodel

import android.os.CountDownTimer
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    companion object {
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 20000L
    }

    private val timer: CountDownTimer

    val name = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val emailAddress = MutableLiveData<String>()

    private val _message: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val message: LiveData<String> get() = _message

    val _isSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSuccess: LiveData<Boolean> get() = _isSuccess

    val animate = MutableLiveData<Boolean>()

    val animateView: LiveData<Boolean> = Transformations.map(animate) {
        it
    }

    private val _isTimerFinish = MutableLiveData<Boolean>()
    val isTimerFinish: LiveData<Boolean>
        get() = _isTimerFinish

    init {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                _isTimerFinish.value = true
            }
        }
//        timer.start()
    }

    fun submitButtonClicked() {
        val msg = checkValidation()
        if (msg.equals("success", true)) {
            _isSuccess.value = true
//            animate.value = true
        } else {
            _message.value = msg
            _isSuccess.value = false
        }
    }

    private fun checkValidation(): String {
        return when {
            name.value.isNullOrBlank() -> return "Please Provide Name"
            phoneNumber.value.isNullOrBlank() -> return "Please Provide Mobile Number"
            emailAddress.value.isNullOrBlank() -> return "Please Provide Emaid Id"
            phoneNumber.value.toString().length < 10 -> return "Please Provide Correct Phone Number"
            Patterns.EMAIL_ADDRESS.matcher(emailAddress.value).matches().not() -> "Please provide Correct Email Id"
            else -> "success"
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
