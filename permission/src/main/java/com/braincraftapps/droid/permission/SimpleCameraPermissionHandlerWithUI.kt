package com.braincraftapps.droid.permission

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SimpleCameraPermissionHandlerWithUI(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null
) : SimplePermissionHandlerWithUI(activity, fragment) {

    companion object {
        val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    }

    init {
        permissionNames = arrayOf("Camera")
        description =
            "This lets you do things like take photos and record videos," +
                    " and enables other features for camera."
    }

    override fun getPermission(): String {
        return PERMISSION_CAMERA
    }

    override fun getPermissions(): Array<String>? {
        return null
    }
}