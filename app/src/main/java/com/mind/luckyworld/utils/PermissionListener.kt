package com.mind.luckyworld.utils

import java.util.ArrayList

interface PermissionListener {
    fun onPermissionsGranted(requestCode: Int, acceptedPermissionList: ArrayList<String>)
    fun onPermissionsDenied(requestCode: Int, deniedPermissionList: ArrayList<String>)
    fun onPermissionDeniedWithNeverAsk(requestCode: Int, deniedPermissionListWithNeverAsk: ArrayList<String>)
}