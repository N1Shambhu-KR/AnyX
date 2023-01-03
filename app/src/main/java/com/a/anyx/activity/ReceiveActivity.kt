package com.a.anyx.activity

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.a.anyx.R
import com.a.anyx.fragment.*
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.interfaces.OnWiFiP2pChanged

class ReceiveActivity:AppCompatActivity(),IOnFragment,OnWiFiP2pChanged,WifiP2pManager.ChannelListener{

    private lateinit var currentFragment:BaseFragment

    private lateinit var connectDeviceFragment: ConnectDeviceFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        if (savedInstanceState == null){

            connectDeviceFragment = ConnectDeviceFragment()

            currentFragment = connectDeviceFragment
        }else{

            val tag = savedInstanceState.getString("curr")
            currentFragment = supportFragmentManager.findFragmentByTag(tag) as BaseFragment
        }

        supportFragmentManager.apply {

            addFragmentOnAttachListener { fragmentManager, fragment ->

                currentFragment = fragment as BaseFragment
            }

            addOnBackStackChangedListener {

                currentFragment = supportFragmentManager.findFragmentById(R.id.activity_receive_navigator) as BaseFragment
            }

            commit {

                replace(R.id.activity_receive_navigator,currentFragment,currentFragment.getTAG())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("curr",currentFragment.getTAG())
        super.onSaveInstanceState(outState)
    }

    //wifi p2p broadcasts

    override fun onStateChanged() {

        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onDeviceState(false)
    }

    override fun onThisDevice(wifiP2pDevice: WifiP2pDevice) {
        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onThisDevice(wifiP2pDevice)
    }

    override fun onDiscoveryStarted() {
        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onDiscoveryStart()
    }

    override fun onDiscoveryStopped() {
        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onDiscoveryStop()
    }

    override fun onDeviceList(wifiP2pDeviceList: WifiP2pDeviceList) {
        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onDeviceList(wifiP2pDeviceList)
    }

    override fun onConnection(wifiP2pInfo: WifiP2pInfo) {
        if (currentFragment is ConnectDeviceFragment) (currentFragment as ConnectDeviceFragment).onConnection(wifiP2pInfo)
    }

    override fun onChannelDisconnected() {
        //
    }

    override fun onFragment(fragment: Fragment) {

    }

    override fun onFragmentVisibility(fragment: Fragment) {

    }
}