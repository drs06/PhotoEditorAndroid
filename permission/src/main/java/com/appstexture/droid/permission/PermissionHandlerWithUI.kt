package com.appstexture.droid.permission

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class PermissionHandlerWithUI(
    private val activity: AppCompatActivity? = null,
    private val fragment: Fragment? = null
) : PermissionHandler(activity, fragment) {

    companion object {
        const val SHARED_PREFS = "PermissionHandler_prefs"
    }

    private val sharedPrefs = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    var permissionNames: Array<String>? = null
        set(value) {
            require(value != null && value.isNotEmpty()) { "There should be at least one name." }
            field = value
        }

    var title: String? = null
        get() {
            return field ?: getPermissionNamesString()?.run {
                "Allow ${getAppName()} to use your phone's ${this.lowercase()}?"
            }
        }

    var description: String? = null
    var denyButtonText = "Deny"
    var allowButtonText = "Allow"

    var appSettingsIndication: String? = null
        get() {
            return field ?: getPermissionNamesString()?.run {
                "To enable ${if (permissionNames!!.size > 1) "these" else "this"}," +
                        " click $appSettingsButtonText below and activate ${this} under the Permissions menu."
            }
        }
    var notNowButtonText = "Not now"
    var appSettingsButtonText = "App Settings"

    fun getAppName() =
        getContext().applicationInfo.loadLabel(getContext().packageManager).toString()

    private fun getPermissionNamesString(): String? {
        return permissionNames?.run {
            if (isEmpty()) {
                null
            } else {
                var s = ""
                for (i in this.indices) {
                    s += if (i == 0) this[i] else if (i == this.size - 1) " and ${this[i]}" else ", ${this[i]}"
                }
                s
            }
        }
    }

    private fun goToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", getContext().packageName, null)
        intent.data = uri
        getContext().startActivity(intent)
    }

    private var dialog: Dialog? = null
    private fun showDialog(normalDialog: Boolean) {
        hideDialog()

        val message: String? = if (normalDialog) {
            description
        } else {
            if (description == null) {
                appSettingsIndication
            } else {
                if (appSettingsIndication == null) {
                    description
                } else {
                    "$description\n\n$appSettingsIndication"
                }
            }
        }

        dialog = MaterialAlertDialogBuilder(getContext(), getDialogStyle())
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(if (normalDialog) denyButtonText else notNowButtonText) { _, _ ->
                hideDialog()
            }
            .setPositiveButton(if (normalDialog) allowButtonText else appSettingsButtonText) { _, _ ->
                if (normalDialog) {
                    shouldLaunchPermission = true
                    launchPermission()
                } else {
                    goToAppSettings()
                }
                hideDialog()
            }.create()
        dialog!!.show()
    }

    private fun hideDialog() {
        dialog?.dismiss()
        dialog = null
    }

    open fun getDialogStyle() = R.style.PermissionDialogTheme

    private var shouldLaunchPermission = false

    override fun requestForSinglePermission(
        permission: String,
        onSinglePermissionListener: OnSinglePermissionListener
    ) {
        hideDialog()
        val granted = isPermissionGranted(permission)
        shouldLaunchPermission = granted
        super.requestForSinglePermission(permission, onSinglePermissionListener)
        if (!granted) {
            showDialog(true)
        }
    }

    override fun requestForMultiplePermissions(
        permissions: Array<String>,
        onMultiplePermissionsListener: OnMultiplePermissionsListener
    ) {
        hideDialog()
        val granted = arePermissionsGranted(permissions)
        shouldLaunchPermission = granted
        super.requestForMultiplePermissions(permissions, onMultiplePermissionsListener)
        if (!granted) {
            showDialog(true)
        }
    }


    private var permissionActivityLaunched = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        if (isCurrentTopActivityPermissionActivity()) {
            permissionActivityLaunched = true
        }
    }

    private fun startCheckingPermissionActivityLaunching() {
        stopCheckingPermissionActivityLaunching()
        handler.postDelayed(runnable, 500)
    }

    private fun stopCheckingPermissionActivityLaunching() {
        handler.removeCallbacks(runnable)
    }

    override fun launchPermission() {
        stopCheckingPermissionActivityLaunching()
        permissionActivityLaunched = false

        if (shouldLaunchPermission) {
            super.launchPermission()
            startCheckingPermissionActivityLaunching()
        }
    }

    override fun onSinglePermissionResult(
        permission: String,
        wasGranted: Boolean,
        shouldShowRequestPermissionRationale: Boolean
    ) {
        stopCheckingPermissionActivityLaunching()
        super.onSinglePermissionResult(permission, wasGranted, shouldShowRequestPermissionRationale)
        if (!wasGranted && !shouldShowRequestPermissionRationale && !permissionActivityLaunched) {
            showDialog(false)
        }
    }

    override fun onMultiplePermissionsResult(
        permissions: Array<String>, wasGranted: Boolean,
        grantedPermissions: Array<String>, deniedPermissions: Array<String>,
        shouldShowRequestPermissionRationale: Boolean
    ) {
        stopCheckingPermissionActivityLaunching()
        super.onMultiplePermissionsResult(
            permissions, wasGranted, grantedPermissions, deniedPermissions,
            shouldShowRequestPermissionRationale
        )
        if (!wasGranted && !shouldShowRequestPermissionRationale && !permissionActivityLaunched) {
            showDialog(false)
        }
    }

    private fun isCurrentTopActivityPermissionActivity(): Boolean {
        return getCurrentTopActivityName() == "com.android.permissioncontroller.permission.ui.GrantPermissionsActivity"
    }

    private fun getCurrentTopActivityName(): String? {
        if (Build.VERSION.SDK_INT < 23) return null
        val am = getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        am?.appTasks?.forEach {
            it.taskInfo.topActivity?.className?.let { s ->
                return s
            }
        }
        return null
    }
}