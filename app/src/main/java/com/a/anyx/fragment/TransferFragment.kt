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
import android.widget.ImageView
import com.a.anyx.R
import com.a.anyx.interfaces.ConnectionListener
import com.a.anyx.interfaces.TransferEventListener
import com.a.anyx.service.SocketCommunicationService
import java.io.File


class TransferFragment : BaseFragment(), TransferEventListener, ConnectionListener {

    private lateinit var socketCommunicationService: SocketCommunicationService

    private val serviceConnectionListener = object :ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as SocketCommunicationService.LocalBinder

            socketCommunicationService = binder.getService()

            socketCommunicationService.setConnectionListener(this@TransferFragment)
            socketCommunicationService.setTransferEventListener(this@TransferFragment)

            val p2pInfo = arguments?.getParcelable<WifiP2pInfo>(P2P_INFO)!!

            if (p2pInfo.isGroupOwner){

                socketCommunicationService.createServer()
            }else{
                socketCommunicationService.createClient(p2pInfo.groupOwnerAddress.hostAddress!!)
            }


        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(context,SocketCommunicationService::class.java)

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

    override fun getTAG(): String? {

        return TAG
    }

    override fun onConnection() {
             val transferType = arguments?.getInt(TRANSFER_TYPE)!!

        if (transferType == SEND){

            socketCommunicationService.send(arguments?.getStringArrayList(FILE_PATHS)!!)
        }else{
            socketCommunicationService.receive()
        }
    }

    override fun onDisconnect() {

    }

    override fun onBackPressed(): Boolean {
        return true
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

    //
    override fun loadImage(imageView: ImageView, position: Int) {

    }

    companion object {

        @JvmField val TAG:String = TransferFragment::class.simpleName!!

        const val FILE_PATHS = "file_paths"
        const val P2P_INFO = "p2p_info"
        const val TRANSFER_TYPE = "transfer_type"

        const val SEND = 1
        const val RECEIVE = 2
    }

}