package com.mind.luckyworld.utils

import android.content.Context
import com.mind.luckyworld.viewmodel.RegisterHomeViewModelFactory
import com.mind.luckyworld.viewmodel.RegisterSuccessViewModelFactory
import com.mind.luckyworld.viewmodel.RegisterViewModelFactory

object InjectorUtils {

    fun provideRegisterHomeViewModelFactory(
        context: Context
    ): RegisterHomeViewModelFactory {
        return RegisterHomeViewModelFactory()
    }

    fun provideRegisterViewModelFactory(context: Context): RegisterViewModelFactory {
        return RegisterViewModelFactory()
    }

    fun provideRegisterSuccessViewModelFactory(
        context: Context
    ): RegisterSuccessViewModelFactory {
        return RegisterSuccessViewModelFactory()
    }
}
