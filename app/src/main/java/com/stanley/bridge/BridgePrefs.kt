package com.stanley.bridge

import android.content.Context

object BridgePrefs {
    private const val FILE = "bridge_prefs"
    private const val KEY_ONBOARDING_SEEN = "onboarding_seen"

    fun isOnboardingSeen(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_SEEN, false)

    fun markOnboardingSeen(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_SEEN, true)
            .apply()
    }
}
