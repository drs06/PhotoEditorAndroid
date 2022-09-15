package com.braincraftapps.droid.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


open class PermissionHandler(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null
) {

    private val singlePermissionContract: ActivityResultLauncher<String>
    private val multiplePermissionContract: ActivityResultLauncher<Array<String>>

    init {
        require(activity != null || fragment != null) {
            "activity and fragment both can't be null."
        }

        require(!(activity != null && fragment != null)) {
            "activity and fragment both can't be non null."
        }

        singlePermissionContract = (activity ?: fragment!!).registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { wasGranted ->
            singlePermission?.let {
                onSinglePermissionResult(it, wasGranted, shouldShowRequestPermissionRationale(it))
            }
        }

        multiplePermissionContract = (activity ?: fragment!!).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsStatusMap ->
            multiplePermissions?.let {
                val grantedPermissions = mutableListOf<String>()
                val deniedPermissions = mutableListOf<String>()
                for (p in it) {
                    if (permissionsStatusMap[p] == true) {
                        grantedPermissions.add(p)
                    } else {
                        deniedPermissions.add(p)
                    }
                }

                val wasGranted = it.size == grantedPermissions.size

                onMultiplePermissionsResult(
                    it, wasGranted,
                    grantedPermissions.toTypedArray(),
                    deniedPermissions.toTypedArray(),
                    shouldShowRequestPermissionRationale(it)
                )
            }
        }
    }

    fun getContext(): Context = activity ?: fragment!!.requireContext()

    fun getLifeCycle() = activity?.lifecycle ?: fragment!!.lifecycle

    protected open fun onSinglePermissionResult(
        permission: String, wasGranted: Boolean, shouldShowRequestPermissionRationale: Boolean
    ) {
        if (wasGranted) {
            onSinglePermissionListener?.onPermissionGranted(permission)
        } else {
            onSinglePermissionListener?.onPermissionDenied(
                permission, shouldShowRequestPermissionRationale
            )
        }
    }

    protected open fun onMultiplePermissionsResult(
        permissions: Array<String>,
        wasGranted: Boolean,
        grantedPermissions: Array<String>,
        deniedPermissions: Array<String>,
        shouldShowRequestPermissionRationale: Boolean
    ) {
        if (wasGranted) {
            onMultiplePermissionsListener?.onAllPermissionsGranted(permissions)

        } else {
            onMultiplePermissionsListener?.onPermissionsDenied(
                permissions,
                grantedPermissions,
                deniedPermissions,
                shouldShowRequestPermissionRationale
            )
        }
    }


    var singlePermission: String? = null
        private set
    var onSinglePermissionListener: OnSinglePermissionListener? = null
        private set

    var multiplePermissions: Array<String>? = null
        private set
    var onMultiplePermissionsListener: OnMultiplePermissionsListener? = null
        private set

    open fun requestForSinglePermission(
        permission: String, onSinglePermissionListener: OnSinglePermissionListener
    ) {
        singlePermission = permission
        this.onSinglePermissionListener = onSinglePermissionListener

        multiplePermissions = null
        onMultiplePermissionsListener = null
        launchPermission()
    }

    open fun requestForMultiplePermissions(
        permissions: Array<String>, onMultiplePermissionsListener: OnMultiplePermissionsListener
    ) {
        require(permissions.size > 1) { "Size of permissions must be > 1." }

        multiplePermissions = permissions
        this.onMultiplePermissionsListener = onMultiplePermissionsListener

        singlePermission = null
        onSinglePermissionListener = null
        launchPermission()
    }

    protected open fun launchPermission() {
        if (singlePermission != null && onSinglePermissionListener != null) {
            singlePermissionContract.launch(singlePermission)
        } else if (multiplePermissions != null && onMultiplePermissionsListener != null) {
            multiplePermissionContract.launch(multiplePermissions)
        }
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            getContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (!isPermissionGranted(p)) return false
        }
        return true
    }

    open fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return activity?.run {
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        } ?: fragment!!.shouldShowRequestPermissionRationale(permission)
    }

    open fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (shouldShowRequestPermissionRationale(p)) return true
        }
        return false
    }

    interface OnSinglePermissionListener {
        fun onPermissionGranted(permission: String)
        fun onPermissionDenied(permission: String, shouldShowRequestPermissionRationale: Boolean)
    }

    interface OnMultiplePermissionsListener {
        fun onAllPermissionsGranted(permissions: Array<String>)
        fun onPermissionsDenied(
            permissions: Array<String>,
            grantedPermissions: Array<String>,
            deniedPermissions: Array<String>,
            shouldShowRequestPermissionRationale: Boolean
        )
    }
}
