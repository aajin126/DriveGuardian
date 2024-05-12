package com.example.poseexercise.backgroundlocationtracking

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        // 구현부
        return callbackFlow {
            if(!context.hasLocationPermission()){
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if(!isGpsEnabled&& !isNetworkEnabled){
                    throw LocationClient.LocationException("Gps is disabled")
                }
            val request= LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)

            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let{ location ->
                        launch { send(location) }
                    }
                }
            }




        }
    }

}