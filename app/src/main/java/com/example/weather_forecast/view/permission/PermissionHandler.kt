package com.example.weather_forecast.view.permission

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHandler(private val activity: ComponentActivity) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    fun init(permissionViewModel: PermissionViewModel) {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[ACCESS_FINE_LOCATION] == true
                    || permissions[ACCESS_COARSE_LOCATION] == true
            when {
                granted -> permissionViewModel.onPermissionGranted()

                !activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ->
                    permissionViewModel.onPermissionPermanentlyDenied()

                else -> permissionViewModel.onPermissionDenied()
            }
        }
    }

    fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            activity, ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
               ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )
    }
}