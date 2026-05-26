package com.stanley.bridge

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = extractUrl()
        if (url == null) {
            fallbackToBrowser()
            return
        }

        lifecycleScope.launch {
            try {
                val parsed = AppleMapsParser.parse(url)
                val googleUri = parsed.toGoogleMapsUri()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleUri))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                // Use chooser so user picks Google Maps, Waze, Uber, etc.
                val chooser = Intent.createChooser(intent, getString(R.string.chooser_open_with))
                if (chooser.resolveActivity(packageManager) != null) {
                    startActivity(chooser)
                } else {
                    startActivity(intent)
                }
            } catch (_: Exception) {
                fallbackToBrowser()
            }
            finish()
        }
    }

    private fun extractUrl(): String? {
        return when (intent?.action) {
            Intent.ACTION_VIEW -> intent.data?.toString()
            Intent.ACTION_SEND -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
                // Extract Apple Maps URL from shared text
                val regex = Regex("""https?://maps\.apple\.com\S+""")
                regex.find(text)?.value
            }
            else -> null
        }
    }

    private fun fallbackToBrowser() {
        val url = intent.data?.toString() ?: extractUrl()
        if (url != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                startActivity(browserIntent)
            } catch (_: Exception) {
                // Nothing we can do
            }
        }
        finish()
    }
}
