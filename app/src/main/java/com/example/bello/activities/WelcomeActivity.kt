package com.example.bello.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.bello.R
import com.example.bello.firebase.FirestoreClass


class WelcomeActivity : AppCompatActivity() {

    private val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with notifications
                showToast("Notification permission granted!")
            } else {
                // Permission denied, show explanation or navigate to settings
                showPermissionDeniedDialog()
            }
        }

        // Check if the permission is already granted
        if (isNotificationPermissionGranted()) {
            showToast("Notification permission already granted!")
        } else {
            // Request the notification permission
            requestNotificationPermission()
        }


        Handler().postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, SignInActivity::class.java))
            }
            finish()
        },2500)

    }

    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, NOTIFICATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isNotificationPermissionGranted()) {
                showToast("Notification permission already granted!")
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, NOTIFICATION_PERMISSION)) {
                    // Explain the importance of the permission to the user
                    showRationaleDialog()
                } else {
                    // Directly request the permission
                    requestPermissionLauncher?.launch(NOTIFICATION_PERMISSION)
                }
            }
        } else {
            showToast("Notification permission is not required on this Android version.")
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("We need your permission to show notifications.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher?.launch(NOTIFICATION_PERMISSION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied permission for notifications. You can grant the permission from the settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS.toUri()
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}