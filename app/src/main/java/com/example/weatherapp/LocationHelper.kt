package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var currentLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            location?.let {
                currentLocation = location
                val city = getCityName(location.latitude, location.longitude)
                stopLocationUpdates()

                cityFetchedCallback?.invoke(city)
            } ?: run {
                Log.e("LocationHelper", "Location is null.")
            }
        }
    }

    private var cityFetchedCallback: ((String?) -> Unit)? = null

    suspend fun requestLocationUpdates(onCityFetched: (String?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onCityFetched("Unknown")
            return
        }

        cityFetchedCallback = onCityFetched

        // Request location updates
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 5000
                fastestInterval = 2000
            },
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Get city name from latitude and longitude
    private fun getCityName(lat: Double, long: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (addresses.isNullOrEmpty()) {
                null
            } else {
                val cityName = addresses[0].locality ?: ""
                val state = addresses[0].adminArea ?: ""
                val country = addresses[0].countryName ?: ""

                val fullCityName = if (state.isNotBlank()) {
                    "$cityName, $state"
                } else {
                    cityName
                }
                fullCityName
            }
        } catch (e: Exception) {
            null
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
