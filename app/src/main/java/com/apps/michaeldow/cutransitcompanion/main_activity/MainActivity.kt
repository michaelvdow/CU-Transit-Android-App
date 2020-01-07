package com.apps.michaeldow.cutransitcompanion.main_activity

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.BetterLocationProvider
import com.apps.michaeldow.cutransitcompanion.Utils.Permissions
import com.apps.michaeldow.cutransitcompanion.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var restart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("darkTheme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        BetterLocationProvider(this)
    }

    override fun onStart() {
        super.onStart()
        showChangeLog()
    }


    private fun showChangeLog() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (prefs.getString("lastChangeLogVersion", "1.0.0") != "3.0.0") {

            val builder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(it, R.style.AlertDialogTheme)
            }

            builder?.setTitle("Release Notes")
                ?.setView(R.layout.dialog_changelog)

            builder?.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                        val editor = prefs.edit()
                        editor.putString("lastChangeLogVersion", "3.0.0")
                        editor.apply()
                    })
            }

            builder?.create()?.show()
        }
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
