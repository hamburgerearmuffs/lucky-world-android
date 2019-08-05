package com.mind.luckyworld.utils

import android.transition.Fade
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager

object BindingAdapters {
    @BindingAdapter("app:animateVisibility")
    @JvmStatic
    fun buttonVisibility(view: View, isSuccess: Boolean) {
        if (view is Button) {
            if (isSuccess) {
                TransitionManager.beginDelayedTransition(view.rootView as ViewGroup)
                view.visibility = View.INVISIBLE
            } else {
                view.visibility = View.VISIBLE
            }
        } else if (view is ProgressBar) {
            if (isSuccess) {
                TransitionManager.beginDelayedTransition(view.rootView as ViewGroup)
                view.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(view.rootView as ViewGroup)
                view.visibility = View.GONE
            }
        }
    }
}