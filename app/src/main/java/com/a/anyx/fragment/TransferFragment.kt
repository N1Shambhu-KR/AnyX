package com.a.anyx.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a.anyx.R
import com.a.anyx.service.SocketCommunicationService
import java.io.File


class TransferFragment : BaseFragment(),SocketCommunicationService.TransferEventListener {

    private lateinit var socketCommunicationService: SocketCommunicationService

    private val serviceConnectionListener = object :ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as SocketCommunicationService.CommunicationBinder

            socketCommunicationService = binder.getService()

            socketCommunicationService.setTransferListener(this@TransferFragment)

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(context,SocketCommunicationService::class.java)
        val info = arguments?.getParcelable<WifiP2pInfo>(OwnerDeviceFragment.WIFI_P2P_INFO)!!

        if (info.isGroupOwner){

            intent.putExtra(OwnerDeviceFragment.WIFI_P2P_INFO,info)

            arguments?.getStringArrayList(OwnerDeviceFragment.FILE_PATHS)!!.also {
                intent.putExtra(OwnerDeviceFragment.FILE_PATHS,it)
            }

        }else{

            intent.putExtra(OwnerDeviceFragment.WIFI_P2P_INFO,info)

        }

        requireContext().bindService(intent,serviceConnectionListener,Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        return arrayListOf()
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

    }

    override fun getTAG(): String? {

        return TAG
    }

    override fun onStartTransfer(file: File) {
         Log.d(TAG,"transfer start")
    }

    override fun onBytesTransferred(file: File, bytesTransferred: Int) {
         Log.d(TAG,"bytes transferred : $bytesTransferred")
    }

    override fun onFinishTransfer(file: File) {
         Log.d(TAG,"transfer complete")
    }

    override fun onStartReceive(file: File) {

    }

    override fun onBytesReceived(file: File, bytesReceived: Int) {

    }

    override fun onFinishReceive(file: File) {

    }

    companion object {

        @JvmField val TAG:String = TransferFragment::class.simpleName!!
    }
}