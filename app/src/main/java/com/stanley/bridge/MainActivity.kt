package com.stanley.bridge

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputUrl: TextInputEditText
    private lateinit var btnConvert: MaterialButton
    private lateinit var resultGroup: LinearLayout
    private lateinit var resultIcon: ImageView
    private lateinit var resultLabel: TextView
    private lateinit var resultText: TextView
    private lateinit var resultActions: LinearLayout
    private lateinit var btnCopy: MaterialButton
    private lateinit var btnOpen: MaterialButton
    private lateinit var statusChip: View

    private var currentGoogleUri: String? = null
    private var currentWebUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BridgePrefs.isOnboardingSeen(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        inputUrl = findViewById(R.id.input_url)
        btnConvert = findViewById(R.id.btn_convert)
        resultGroup = findViewById(R.id.result_group)
        resultIcon = findViewById(R.id.result_icon)
        resultLabel = findViewById(R.id.result_label)
        resultText = findViewById(R.id.result_text)
        resultActions = findViewById(R.id.result_actions)
        btnCopy = findViewById(R.id.btn_copy)
        btnOpen = findViewById(R.id.btn_open)
        statusChip = findViewById(R.id.status_chip)

        statusChip.setOnClickListener { openDefaultAppSettings() }

        inputUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                btnConvert.isEnabled = looksLikeAppleMapsLink(s?.toString())
            }
        })

        btnConvert.setOnClickListener { convert() }

        btnCopy.setOnClickListener {
            val url = currentWebUrl ?: return@setOnClickListener
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Bridge link", url))
            btnCopy.setText(R.string.btn_copy_done)
            btnCopy.postDelayed({ btnCopy.setText(R.string.btn_copy) }, 1200)
        }

        btnOpen.setOnClickListener {
            val uri = currentGoogleUri ?: return@setOnClickListener
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(Intent.createChooser(intent, getString(R.string.chooser_open_with)))
        }
    }

    private fun convert() {
        val url = inputUrl.text?.toString()?.trim().orEmpty()
        if (!looksLikeAppleMapsLink(url)) {
            showError(getString(R.string.error_not_apple_maps))
            return
        }

        btnConvert.isEnabled = false
        btnConvert.setText(R.string.btn_convert_loading)

        lifecycleScope.launch {
            try {
                val parsed = AppleMapsParser.parse(url)
                currentGoogleUri = parsed.toGoogleMapsUri()
                currentWebUrl = parsed.toGoogleMapsWebUrl()
                showResult(currentWebUrl!!)
            } catch (_: Exception) {
                showError(getString(R.string.error_default))
            } finally {
                btnConvert.setText(R.string.btn_convert)
                btnConvert.isEnabled = looksLikeAppleMapsLink(inputUrl.text?.toString())
            }
        }
    }

    private fun showResult(webUrl: String) {
        resultIcon.setImageResource(R.drawable.ic_status_check)
        resultIcon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.bridge_ink))
        resultLabel.setText(R.string.result_label)
        resultLabel.setTextColor(getColor(R.color.bridge_ink))
        resultText.text = webUrl
        resultText.setTextColor(getColor(R.color.bridge_ink_muted))
        resultText.visibility = View.VISIBLE
        resultActions.visibility = View.VISIBLE
        revealResultGroup()
    }

    private fun showError(message: String) {
        currentGoogleUri = null
        currentWebUrl = null
        resultIcon.setImageResource(R.drawable.ic_status_alert)
        resultIcon.imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.bridge_error))
        resultLabel.text = message
        resultLabel.setTextColor(getColor(R.color.bridge_error))
        resultText.visibility = View.GONE
        resultActions.visibility = View.GONE
        revealResultGroup()
    }

    private fun revealResultGroup() {
        if (resultGroup.visibility == View.VISIBLE) return
        if (animationsEnabled()) {
            resultGroup.alpha = 0f
            resultGroup.visibility = View.VISIBLE
            resultGroup.animate().alpha(1f).setDuration(200L).start()
        } else {
            resultGroup.alpha = 1f
            resultGroup.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatusChip()
    }

    private fun refreshStatusChip() {
        statusChip.visibility = if (DomainStatus.isVerifiedForAppleMaps(this)) View.GONE else View.VISIBLE
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

    private fun animationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ValueAnimator.areAnimatorsEnabled()
        } else {
            true
        }
    }

    private fun looksLikeAppleMapsLink(s: String?): Boolean {
        val trimmed = s?.trim().orEmpty()
        return trimmed.contains("maps.apple.com", ignoreCase = true)
    }
}
