package com.a.anyx.service

import android.app.Service
import android.content.Intent
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Binder
import android.os.IBinder
import com.a.anyx.fragment.OwnerDeviceFragment
import java.io.File


class SocketCommunicationService() : Service() {

    interface TransferEventListener{

        fun onStartTransfer(file: File)

        fun onFinishTransfer(file: File)

        fun onBytesTransferred(file: File,bytesTransferred:Int)

        fun onBytesReceived(file: File,bytesReceived:Int)

        fun onFinishReceive(file: File)

        fun onStartReceive(file: File)
    }

    companion object{

        const val PORT = 8888
    }

    inner class CommunicationBinder():Binder(){

        fun getService():SocketCommunicationService = this@SocketCommunicationService
    }

    private lateinit var transferEventListener:TransferEventListener

    fun setTransferListener(transferEventListener: TransferEventListener){
        this.transferEventListener = transferEventListener
    }

    override fun onBind(intent: Intent?): IBinder?{

        val info = intent?.getParcelableExtra<WifiP2pInfo>(OwnerDeviceFragment.WIFI_P2P_INFO)!!


        return CommunicationBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

    }
}