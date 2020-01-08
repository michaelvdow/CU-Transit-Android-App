package com.apps.michaeldow.cutransitcompanion.Utils

import android.content.*
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.apps.michaeldow.cutransitcompanion.R

class AppRater {

    companion object {
        val DAYS_UNTIL_PROMPT = 3
        val LAUNCHES_UNTIL_PROMPT = 5

        fun appLaunched(context: Context) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            if (prefs.getBoolean(SharedPreferenceKeys.DONT_SHOW_AGAIN, false)) {
                return
            }
            val editor = prefs.edit()

            // Update number of launches
            val launchCount = prefs.getLong(SharedPreferenceKeys.NUMBER_OF_LAUNCHES, 0) + 1
            editor.putLong(SharedPreferenceKeys.NUMBER_OF_LAUNCHES, launchCount)

            // Update first launch date
            var firstLaunch = prefs.getLong(SharedPreferenceKeys.DATE_OF_FIRST_LAUNCH, 0)
            if (firstLaunch == 0L) {
                firstLaunch = System.currentTimeMillis()
                editor.putLong(SharedPreferenceKeys.DATE_OF_FIRST_LAUNCH, firstLaunch)
            }

            // Check if number of days passed
            if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
                if (System.currentTimeMillis() >= firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                    showDialog(context, editor)
                }
            }

            editor.apply()
        }

        private fun showDialog(context: Context, editor: SharedPreferences.Editor) {
            val builder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(context, R.style.RateDialogTheme)
            }

            builder?.setTitle("Rate CU Transit")
                ?.setMessage(R.string.rate_message)

            builder?.apply {
                setPositiveButton(R.string.button_rate,
                    DialogInterface.OnClickListener { dialog, id ->
                        editor.putBoolean(SharedPreferenceKeys.DONT_SHOW_AGAIN, true)
                        editor.commit()
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.apps.michaeldow.cutransitcompanion")))
                        } catch (e: ActivityNotFoundException) {
                           context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.apps.michaeldow.cutransitcompanion")))
                        }
                        dialog.dismiss()
                    })
                setNegativeButton(R.string.button_no_rate,
                    DialogInterface.OnClickListener { dialog, id ->
                        editor.putBoolean(SharedPreferenceKeys.DONT_SHOW_AGAIN, true)
                        editor.commit()
                        dialog.dismiss()
                    })

                setNeutralButton(R.string.button_remind_me_later,
                    DialogInterface.OnClickListener { dialog, id ->
                        editor.putLong(SharedPreferenceKeys.NUMBER_OF_LAUNCHES, 0)
                        editor.apply()
                        dialog.dismiss()
                    })
            }

            builder?.create()?.show()
        }
    }
}