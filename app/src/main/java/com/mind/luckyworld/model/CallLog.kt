package com.mind.luckyworld.model

import java.io.Serializable

data class CallLog(
    val number: String,
    val duration: String,
    val date: String,
    val type: String
) : Serializable