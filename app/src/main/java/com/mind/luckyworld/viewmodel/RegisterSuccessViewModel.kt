package com.mind.luckyworld.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

class RegisterSuccessViewModel : ViewModel() {

    companion object {
        private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100)
        private val NO_BUZZ_PATTERN = longArrayOf(0)
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 2000L
    }
    private lateinit var timer: CountDownTimer

    var timerData:Int = 0

    private val _eventBuzz = MutableLiveData<LongArray>()

    val eventBuzz: LiveData<LongArray> = _eventBuzz

    init {
        _eventBuzz.value = CORRECT_BUZZ_PATTERN
    }

    fun startTimer(){
        timer = object : CountDownTimer(
            COUNTDOWN_TIME,
            ONE_SECOND
        ) {

            override fun onTick(millisUntilFinished: Long) {
                timerData = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                timerData = 0
            }
        }
        timer.start()
    }

    fun onBuzzComplete() {
//        _eventBuzz.value = NO_BUZZ_PATTERN
    }
}
