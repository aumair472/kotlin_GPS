package com.geosnap.core.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.geosnap.core.common.DispatcherProvider
import com.geosnap.core.model.PostalAddress
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Reverse geocoding behind an interface so the camera/report layers never touch Geocoder directly
 * (and never from a composable). Returns null on any failure/no-result — addresses are never faked.
 */
interface AddressResolver {
    suspend fun resolve(latitude: Double, longitude: Double): PostalAddress?
}

class AndroidAddressResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider,
) : AddressResolver {

    override suspend fun resolve(latitude: Double, longitude: Double): PostalAddress? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resolveAsync(geocoder, latitude, longitude)
            } else {
                withContext(dispatchers.io) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()?.toPostal()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun resolveAsync(geocoder: Geocoder, lat: Double, lon: Double): PostalAddress? =
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lon, 1) { results ->
                cont.resume(results.firstOrNull()?.toPostal())
            }
        }
}

private fun android.location.Address.toPostal(): PostalAddress {
    val formatted = (0..maxAddressLineIndex.coerceAtLeast(0))
        .mapNotNull { runCatching { getAddressLine(it) }.getOrNull() }
        .firstOrNull { !it.isNullOrBlank() }
        ?: listOfNotNull(locality, adminArea, countryName).joinToString(", ").ifBlank { null }
    return PostalAddress(
        locality = locality,
        adminArea = adminArea,
        countryCode = countryCode,
        formatted = formatted,
    )
}
