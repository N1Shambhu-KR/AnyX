package com.a.anyx.interfaces

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo


interface OnWiFiP2pChanged{

    fun onStateChanged()
    fun onDeviceList(wifiP2pDeviceList: WifiP2pDeviceList)
    fun onThisDevice(wifiP2pDevice: WifiP2pDevice)
    fun onConnection(wifiP2pInfo: WifiP2pInfo)

    fun onDiscoveryStarted()
    fun onDiscoveryStopped()
}
