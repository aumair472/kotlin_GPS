package com.geosnap.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Foreground-only location access (no background permission — PERMISSIONS_PRIVACY.md). Behind an
 * interface so capture logic and ViewModels stay testable without Play Services.
 */
interface LocationGateway {
    fun hasAnyLocationPermission(): Boolean
    fun hasFineLocationPermission(): Boolean
    /** Continuous foreground updates while collected; emits null only if a reading can't be mapped. */
    fun locationUpdates(): Flow<LocationFix?>
    /** One-shot high-accuracy request with a bounded wait; null if unavailable. */
    suspend fun currentLocation(): LocationFix?
}

@Singleton
class FusedLocationGateway @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient,
) : LocationGateway {

    override fun hasAnyLocationPermission(): Boolean =
        hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || hasFineLocationPermission()

    override fun hasFineLocationPermission(): Boolean =
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission") // Caller guarantees a granted permission before collecting.
    override fun locationUpdates(): Flow<LocationFix?> = callbackFlow {
        if (!hasAnyLocationPermission()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val priority = if (hasFineLocationPermission()) Priority.PRIORITY_HIGH_ACCURACY
        else Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val request = LocationRequest.Builder(priority, 2_000L)
            .setMinUpdateIntervalMillis(1_000L)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setWaitForAccurateLocation(false)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toFix()) }
            }
        }
        client.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose { client.removeLocationUpdates(callback) }
    }

    @SuppressLint("MissingPermission")
    override suspend fun currentLocation(): LocationFix? {
        if (!hasAnyLocationPermission()) return null
        val cts = CancellationTokenSource()
        val request = CurrentLocationRequest.Builder()
            .setPriority(
                if (hasFineLocationPermission()) Priority.PRIORITY_HIGH_ACCURACY
                else Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            )
            .setDurationMillis(8_000L)
            .setMaxUpdateAgeMillis(10_000L)
            .build()
        return suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(request, cts.token)
                .addOnSuccessListener { location -> cont.resume(location?.toFix()) }
                .addOnFailureListener { cont.resume(null) }
            cont.invokeOnCancellation { cts.cancel() }
        }
    }
}

private fun Location.toFix(): LocationFix = LocationFix(
    latitude = latitude,
    longitude = longitude,
    altitudeMeters = if (hasAltitude()) altitude else null,
    horizontalAccuracyMeters = if (hasAccuracy()) accuracy else null,
    elapsedAt = Instant.ofEpochMilli(time),
    isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) isMock else @Suppress("DEPRECATION") isFromMockProvider,
    provider = provider,
)
