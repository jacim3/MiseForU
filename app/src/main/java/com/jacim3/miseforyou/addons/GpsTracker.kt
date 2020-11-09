package com.jacim3.miseforyou.addons

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat

class GpsTracker(private val mContext: Context) : Service(),
    LocationListener {

    // java의 경우 생성자를 통하여, MainActivity에서 호출 시 매개변수로 받은 Context를 생성자를 통하여 setter해야 하나,
    // Kotlin 에서는 프로퍼티를 이용, 자동으로 getter와 setter가 동작하므로, 이에 대한 과정이 생략된다. -> 생성자에 val var선언을 통하여 자동 초기화를 실행할 수 있음

    private var location: Location? = null
    private var locationManager: LocationManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    //우선 선언을 먼저 하고, 나중에 초기화 하기 위하여. 초기화 전 까진 이 변수에 접근이 불가

    private fun getLocation(): Location? {
        try {
            //mContext로서, 컨텍스트를 제대로 지정해 주지 않았을 경우 불러올 수 없었다.
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled: Boolean =
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                val hasFineLocationPermission: Int = ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                val hasCoarseLocationPermission: Int = ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
                ) {

                } else
                    return null

                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                    )
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this
                        )
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("@@@", "aaa" + e.toString())
        }
        return location
    }

    fun get_Latitude() : Double{
        if (location != null){
            latitude = location!!.latitude
        }
        return latitude
    }

    fun get_Longitude() : Double{
        if(location != null){
            longitude = location!!.longitude
        }
        return longitude
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    fun stopUsingGPS() {
        if(locationManager != null)
            locationManager!!.removeUpdates(this)
    }

    companion object{
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10.0F
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    }

    init {
        getLocation()
    }
}
