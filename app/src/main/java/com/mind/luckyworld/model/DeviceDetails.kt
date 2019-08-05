package com.mind.luckyworld.model

import java.io.Serializable

data class DeviceDetails(
    val deviceName: String,
    val deviceMac: String,
    val deviceLocation: String,
    val deviceMemoryUsage: String,
    val deviceBatteryLevel: String,
    var phoneNumber: String = ""
) : Serializable