package com.mind.luckyworld.model

import java.io.Serializable

data class User(
    val name: String,
    val number: String,
    val email: String
): Serializable