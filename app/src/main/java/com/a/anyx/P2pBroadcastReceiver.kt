package com.a.anyx

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.a.anyx.interfaces.OnWiFiP2pChanged


class P2pBroadcastReceiver(private val activity: Activity,
                               private val wifiP2pManager: WifiP2pManager,
                               private val channel:WifiP2pManager.Channel) :BroadcastReceiver(){


    private val detectInterface = activity as OnWiFiP2pChanged

    override fun onReceive(context: Context?, intent: Intent?) {


        when(intent!!.action){

            WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION->{

                val discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE,-1)

                if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
                    detectInterface.onDiscoveryStarted()
                }else if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED){
                    detectInterface.onDiscoveryStopped()
                }

            }

            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION->{
                detectInterface.onStateChanged()
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION->{

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    wifiP2pManager.requestPeers(channel) {
                        detectInterface.onDeviceList(it)
                    }
                }

            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION->{
               val myDevice = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)

                myDevice?.also {
                    detectInterface.onThisDevice(myDevice)
                }

            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION->{

                wifiP2pManager.requestConnectionInfo(channel) {
                    detectInterface.onConnection(it)
                }
            }
        }
    }
}