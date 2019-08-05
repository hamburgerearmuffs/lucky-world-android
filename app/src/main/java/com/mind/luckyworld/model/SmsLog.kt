package com.mind.luckyworld.model

import java.io.Serializable

data class SmsLog(
    val id: String,
    val address: String,
    val message: String,
    val time: String,
    val type: String
) : Serializable