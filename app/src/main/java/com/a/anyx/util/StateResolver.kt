package com.a.anyx.util

import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager

class StateResolver(context: Context) {

    private val wifManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val bluetoothManager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getWifiState():Boolean{
        return wifManager.isWifiEnabled
    }

    fun getBluetoothState():Boolean{
        return bluetoothManager.adapter.isEnabled
    }

    fun getLocationState():Boolean{
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}