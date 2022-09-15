package com.braincraftapps.droid.permission

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class SimplePermissionHandlerWithUI(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null
) : PermissionHandlerWithUI(activity, fragment) {

    abstract fun getPermission(): String?

    abstract fun getPermissions(): Array<String>?

    fun isPermissionGranted(): Boolean {
        if (getPermission() != null) return isPermissionGranted(getPermission()!!)
        return arePermissionsGranted(getPermissions()!!)
    }

    fun requestForPermission(onPermissionListener: OnPermissionListener) {
        if (getPermissions() != null) {
            requestForMultiplePermissions(
                getPermissions()!!,
                object : OnMultiplePermissionsListener {
                    override fun onAllPermissionsGranted(permissions: Array<String>) {
                        onPermissionListener.onPermissionGranted()
                    }

                    override fun onPermissionsDenied(
                        permissions: Array<String>,
                        grantedPermissions: Array<String>,
                        deniedPermissions: Array<String>,
                        shouldShowRequestPermissionRationale: Boolean
                    ) {
                        onPermissionListener.onPermissionDenied()
                    }

                })
        } else {
            requestForSinglePermission(getPermission()!!, object :
                OnSinglePermissionListener {
                override fun onPermissionGranted(permission: String) {
                    onPermissionListener.onPermissionGranted()
                }

                override fun onPermissionDenied(
                    permission: String,
                    shouldShowRequestPermissionRationale: Boolean
                ) {
                    onPermissionListener.onPermissionDenied()
                }

            })
        }
    }

    fun requestForPermissionWithoutUI(onPermissionListener: OnPermissionListener) {
        if (isPermissionGranted()) {
            onPermissionListener.onPermissionGranted()
        } else {
            onPermissionListener.onPermissionDenied()
        }
    }

    interface OnPermissionListener {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }
}