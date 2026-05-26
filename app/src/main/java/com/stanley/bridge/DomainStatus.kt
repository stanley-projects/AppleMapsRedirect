package com.stanley.bridge

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build

object DomainStatus {

    private const val APPLE_MAPS_HOST = "maps.apple.com"

    /**
     * Returns true if the user has explicitly enabled Bridge as the default
     * handler for maps.apple.com under Android 12+'s domain verification system.
     *
     * On Android <12 there's no equivalent user-state API; we return false so
     * the status chip nudges the user to the older settings path.
     */
    fun isVerifiedForAppleMaps(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
        return try {
            val manager = context.getSystemService(DomainVerificationManager::class.java)
                ?: return false
            val state = manager.getDomainVerificationUserState(context.packageName)
                ?: return false
            val hostState = state.hostToStateMap[APPLE_MAPS_HOST] ?: return false
            hostState == DomainVerificationUserState.DOMAIN_STATE_SELECTED ||
                hostState == DomainVerificationUserState.DOMAIN_STATE_VERIFIED
        } catch (_: Exception) {
            false
        }
    }
}
