package com.apps.michaedow.cutransit.main_activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.Utils.BetterLocationProvider
import com.apps.michaedow.cutransit.Utils.Permissions
import com.apps.michaedow.cutransit.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var restart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        BetterLocationProvider(this)
    }


    // Request location permissions
    // Restart if failed to get permission
    fun requestPermission(restart: Boolean) {
        this.restart = restart
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                Permissions.COARSE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Permissions.FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                Permissions.ACCESS_NETWORK_STATE)
        }
    }

    // Restart activity if permission granted or restart if true
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == Permissions.COARSE_LOCATION || requestCode == Permissions.FINE_LOCATION)
            && PackageManager.PERMISSION_GRANTED in grantResults) {
            recreate()
        } else if (restart) {
            recreate()
        }
    }


}
