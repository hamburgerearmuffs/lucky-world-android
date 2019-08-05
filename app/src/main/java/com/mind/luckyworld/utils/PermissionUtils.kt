package com.mind.luckyworld.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mind.luckyworld.constant.Permission
import java.util.*
import kotlin.properties.Delegates

class PermissionUtils(builder: Builder) {

    private var permissionsToAsk: ArrayList<String>
    private var rationalMessage: String
    private var permissionListener: PermissionListener? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var context: Context
    private var isFragment: Boolean by Delegates.notNull()
    private var shouldShowRationaleDialog: Boolean = false
    private var requestCode: Int by Delegates.notNull()

    /**
     * Entry point of [PermissionHelper]
     *
     * @return instance of [Builder]
     */

    init {
        this.fragment = builder.fragment
        this.activity = builder.activity
        this.requestCode = builder.requestCode
        this.rationalMessage = builder.rationalMessage
        this.permissionsToAsk = builder.permissionsToAsk
        this.permissionListener = builder.permissionListener
        this.context = builder.context!!
        this.isFragment = builder.isFragment
    }

    companion object {
        fun Builder(): IWith {
            return Builder()
        }
    }

    /**
     * Method that invokes permission dialog, if permission is already granted or
     * denied (with never asked ticked) then the result is delivered without showing any dialog.
     */
    fun requestPermissions() {
        if (!hasPermissions(context, *permissionsToAsk.toTypedArray())) {
            if (shouldShowRationale(*permissionsToAsk.toTypedArray())) {
                request()
                /*showDialog(
                    context,
                    rationalMessage,
                    "Ok",
                    "Cancel",
                    DialogInterface.OnClickListener { _, _ -> request() },
                    DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })!!.show()*/
            } else {
                request()
            }
        } else {
            permissionListener!!.onPermissionsGranted(requestCode, permissionsToAsk)
        }
    }

    /* Shows a dialog for permission */
    private fun request() {
        if (isFragment) {
            fragment!!.requestPermissions(permissionsToAsk.toTypedArray(), requestCode)
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                permissionsToAsk.toTypedArray(),
                requestCode
            )
        }
    }

    /* Check whether any permission is denied before, if yes then we show a rational dialog for explanation */
    private fun shouldShowRationale(vararg permissions: String): Boolean {
        // Todo : Improve, check if this check can be done with only one call (ActivityCompat) for both fragment and activity
        if (isFragment) {
            for (permission in permissions) {
                if (fragment!!.shouldShowRequestPermissionRationale(permission)) {
                    shouldShowRationaleDialog = true
                }
            }
        } else {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                    shouldShowRationaleDialog = true
                }
            }
        }
        return shouldShowRationaleDialog
    }

    /* Check if we already have the permission */
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * Called by the user when he gets the call in Activity/Fragment
     *
     * @param reqCode      Request Code
     * @param permissions  List of permissions
     * @param grantResults Permission grant result
     */
    fun onRequestPermissionsResult(
        reqCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == reqCode) {
            val grantedPermissionList = ArrayList<String>()
            val deniedPermissionList = ArrayList<String>()
            val neverAskPermissionList = ArrayList<String>()
            for (i in grantResults.indices) {
                val grantResult = grantResults[i]
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissionList.add(permissions[i])
                } else {
                    if (shouldShowRequestPermissionRationale(context as Activity, permissions[i])) {
                        deniedPermissionList.add(permissions[i])
                    } else {
                        neverAskPermissionList.add(permissions[i])
                    }
                }
            }

            if (!grantedPermissionList.isEmpty()) {
                permissionListener!!.onPermissionsGranted(requestCode, grantedPermissionList)
            }
            if (!deniedPermissionList.isEmpty()) {
                permissionListener!!.onPermissionsDenied(requestCode, deniedPermissionList)
            }
            if (!neverAskPermissionList.isEmpty()) {
                permissionListener!!.onPermissionDeniedWithNeverAsk(
                    requestCode,
                    neverAskPermissionList
                )
            }
        }
    }


    /**
     * [Builder] class for [PermissionUtils].
     * Use only this class to create a new instance of [PermissionUtils]
     */

    class Builder : IWith, IRequestCode, IPermissionResultCallback, IAskFor, IBuild {
        lateinit var permissionsToAsk: ArrayList<String>
        lateinit var rationalMessage: String
        var permissionListener: PermissionListener? = null
        var activity: Activity? = null
        var fragment: Fragment? = null
        var context: Context? = null
        var requestCode = -1
        var isFragment: Boolean = false

        override fun with(activity: Activity): IRequestCode {
            this.activity = activity
            this.context = activity
            isFragment = false
            return this
        }

        override fun with(fragment: Fragment): IRequestCode {
            this.fragment = fragment
            this.context = fragment.activity
            isFragment = true
            return this
        }

        override fun requestCode(requestCode: Int): IPermissionResultCallback {
            this.requestCode = requestCode
            return this
        }

        override fun setPermissionResultCallback(permissionListener: PermissionListener): IAskFor {
            this.permissionListener = permissionListener
            return this
        }

        override fun askFor(vararg permission: Permission): IBuild {
            permissionsToAsk = ArrayList()
            for (mPermission in permission) {
                when (mPermission) {
                    Permission.CALLLOG -> {
                        permissionsToAsk.add(Manifest.permission.READ_CALL_LOG)
                    }
                    Permission.LOCATION -> {
                        permissionsToAsk.add(Manifest.permission.ACCESS_FINE_LOCATION)
                        permissionsToAsk.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                    Permission.PHONE -> permissionsToAsk.add(Manifest.permission.READ_PHONE_STATE)
                    Permission.SMS -> {
                        permissionsToAsk.add(Manifest.permission.READ_SMS)
                    }
                }
            }
            return this
        }

        override fun rationalMessage(message: String): PermissionUtils.IBuild {
            this.rationalMessage = message
            return this
        }

        override fun build(): PermissionUtils {
            return when {
                this.permissionListener == null -> throw NullPointerException("Permission listener can not be null")
                this.context == null -> throw NullPointerException("Context can not be null")
                this.permissionsToAsk.size == 0 -> throw IllegalArgumentException("Not asking for any permission. At least one permission is expected before calling build()")
                this.requestCode == -1 -> throw IllegalArgumentException("Request code is missing")
                else -> PermissionUtils(this)
            }
        }

    }

    /*Interfaces for builder to make some methods must/required*/

    interface IWith {
        fun with(activity: Activity): IRequestCode

        fun with(fragment: Fragment): IRequestCode
    }

    interface IRequestCode {
        fun requestCode(requestCode: Int): IPermissionResultCallback
    }

    interface IPermissionResultCallback {
        fun setPermissionResultCallback(permissionListener: PermissionListener): IAskFor
    }

    interface IAskFor {
        fun askFor(vararg permission: Permission): IBuild
    }

    interface IBuild {
        fun rationalMessage(message: String): IBuild

        fun build(): PermissionUtils
    }
}