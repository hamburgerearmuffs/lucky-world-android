package com.mind.luckyworld.view.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mind.luckyworld.R
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        var emailAddress = arrayOf(
            "Sharma, Vikas (AESINDIA)",
            "Agarawal, Vikas",
            "Gupta, Vikas(AESINDIA)",
            "Sharma, Vikas(MSSL)",
            "Sharma, Vikas K. (MIND)"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, emailAddress)
        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setTextColor(Color.RED)
    }
}
