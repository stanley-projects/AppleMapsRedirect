package com.stanley.bridge

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class ParsedLocation(
    val lat: Double? = null,
    val lng: Double? = null,
    val query: String? = null,
    val label: String? = null,
    val isDirections: Boolean = false
) {
    fun toGoogleMapsUri(): String {
        val labelPart = label?.takeIf { it.isNotBlank() }?.let { "(${Uri.encode(it)})" } ?: ""
        return when {
            isDirections && lat != null && lng != null ->
                "google.navigation:q=$lat,$lng"
            lat != null && lng != null ->
                "geo:$lat,$lng?q=$lat,$lng$labelPart"
            !query.isNullOrBlank() ->
                "geo:0,0?q=${Uri.encode(query)}"
            else -> throw IllegalStateException("No location data to convert")
        }
    }

    fun toGoogleMapsWebUrl(): String {
        return when {
            isDirections && lat != null && lng != null ->
                "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"
            lat != null && lng != null ->
                "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
            !query.isNullOrBlank() ->
                "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"
            else -> throw IllegalStateException("No location data to convert")
        }
    }
}

object AppleMapsParser {

    suspend fun parse(urlString: String): ParsedLocation {
        val resolved = resolveRedirects(urlString.trim())
        return parseUrl(resolved)
    }

    private fun parseUrl(urlString: String): ParsedLocation {
        val uri = Uri.parse(urlString)

        // Modern Apple URLs pair coordinate=lat,lng with name=...
        // When both exist, coordinate is the location and name becomes a display label.
        val nameLabel = uri.getQueryParameter("name")?.takeIf { it.isNotBlank() }

        // 1. coordinate (modern) or ll (legacy) — both carry lat,lng.
        val coords = uri.getQueryParameter("coordinate") ?: uri.getQueryParameter("ll")
        coords?.let {
            val parts = it.split(",")
            if (parts.size == 2) {
                val lat = parts[0].trim().toDoubleOrNull()
                val lng = parts[1].trim().toDoubleOrNull()
                if (lat != null && lng != null) {
                    return ParsedLocation(lat = lat, lng = lng, label = nameLabel)
                }
            }
        }

        // 2. daddr= (directions destination).
        uri.getQueryParameter("daddr")?.let { daddr ->
            val parts = daddr.split(",")
            if (parts.size == 2) {
                val lat = parts[0].trim().toDoubleOrNull()
                val lng = parts[1].trim().toDoubleOrNull()
                if (lat != null && lng != null) {
                    return ParsedLocation(lat = lat, lng = lng, label = nameLabel, isDirections = true)
                }
            }
            return ParsedLocation(query = daddr, isDirections = true)
        }

        // 3. address=
        uri.getQueryParameter("address")?.let { address ->
            if (address.isNotBlank()) return ParsedLocation(query = address)
        }

        // 4. q= (legacy) or name= alone (modern) — query / place name. May itself be lat,lng.
        val placeQuery = uri.getQueryParameter("q") ?: nameLabel
        placeQuery?.let { q ->
            if (q.isNotBlank()) {
                val parts = q.split(",")
                if (parts.size == 2) {
                    val lat = parts[0].trim().toDoubleOrNull()
                    val lng = parts[1].trim().toDoubleOrNull()
                    if (lat != null && lng != null) {
                        return ParsedLocation(lat = lat, lng = lng)
                    }
                }
                return ParsedLocation(query = q)
            }
        }

        // 5. saddr= (source address, rare).
        uri.getQueryParameter("saddr")?.let { saddr ->
            if (saddr.isNotBlank()) return ParsedLocation(query = saddr)
        }

        // 6. Path-based: /place/Eiffel+Tower (legacy slug form).
        val path = uri.path
        if (!path.isNullOrBlank() && path != "/") {
            val placePath = path.removePrefix("/place/").removePrefix("/")
            if (placePath.isNotBlank() && placePath != path.removePrefix("/")) {
                return ParsedLocation(query = Uri.decode(placePath))
            }
        }

        throw IllegalArgumentException(
            "Could not extract location from URL. Supported parameters: coordinate, ll, q, name, address, daddr."
        )
    }

    private suspend fun resolveRedirects(urlString: String): String {
        // If it already has query params we can parse, skip the network call.
        val uri = Uri.parse(urlString)
        val hasParams = uri.getQueryParameter("coordinate") != null ||
                uri.getQueryParameter("ll") != null ||
                uri.getQueryParameter("q") != null ||
                uri.getQueryParameter("name") != null ||
                uri.getQueryParameter("address") != null ||
                uri.getQueryParameter("daddr") != null
        if (hasParams) return urlString

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"

                val responseCode = conn.responseCode
                if (responseCode in 301..303 || responseCode == 307 || responseCode == 308) {
                    val location = conn.getHeaderField("Location")
                    conn.disconnect()
                    if (!location.isNullOrBlank()) {
                        return@withContext resolveRedirects(location)
                    }
                }
                conn.disconnect()
                urlString
            } catch (_: Exception) {
                urlString
            }
        }
    }
}
