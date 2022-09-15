package com.appstexture.droid.permission

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class SimpleMediaPermissionHandlerWithUI(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null
) : SimplePermissionHandlerWithUI(activity, fragment) {

    companion object {
        val PERMISSION_STORAGE = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    init {
        permissionNames = arrayOf("Storage")
        description =
            "This lets you choose from your camera roll, and enables other features for photos and videos."
    }

    override fun getPermission(): String? {
        return null
    }

    override fun getPermissions(): Array<String>? {
        return PERMISSION_STORAGE
    }

}