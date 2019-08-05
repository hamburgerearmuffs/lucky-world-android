package com.mind.luckyworld.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

class RegisterHomeViewModel : ViewModel() {

    init {

    }

    private val _isRegisterClick: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isRegisterClick: LiveData<Boolean> get() = _isRegisterClick

    fun onRegisterClick() {
        _isRegisterClick.value = true
    }

    fun setClickToFalse(){
        _isRegisterClick.value = false
    }
}
