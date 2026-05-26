package com.stanley.bridge

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        findViewById<MaterialButton>(R.id.btn_enable).setOnClickListener {
            BridgePrefs.markOnboardingSeen(this)
            openDefaultAppSettings()
        }

        findViewById<MaterialButton>(R.id.btn_skip).setOnClickListener {
            BridgePrefs.markOnboardingSeen(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // If the user came back from the settings deep link and enabled the
        // domain, dismiss onboarding automatically.
        if (BridgePrefs.isOnboardingSeen(this) && DomainStatus.isVerifiedForAppleMaps(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun openDefaultAppSettings() {
        Toast.makeText(this, R.string.settings_hint, Toast.LENGTH_LONG).show()
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            )
        }
    }
}
