package com.a.anyx.fragment

import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import com.a.anyx.P2pBroadcastReceiver
import com.a.anyx.R
import com.a.anyx.interfaces.OnWiFiP2pChanged
import com.a.anyx.view.SignalRipple
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

@SuppressWarnings("MissingPermission")
class WaitDeviceFragment : BaseFragment(), OnWiFiP2pChanged {


    private lateinit var qrBitmap: Bitmap

    private lateinit var qrImage: ImageView

    private lateinit var wifiP2pManager: WifiP2pManager

    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var signalRipple: SignalRipple

    private lateinit var p2pBroadcastReceiver: P2pBroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        wifiP2pManager =
            requireContext().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

        channel = wifiP2pManager.initialize(requireContext(), Looper.getMainLooper()) {}

        //register a broadcast for receiving intents when there is change in the framework
        p2pBroadcastReceiver = P2pBroadcastReceiver(this, wifiP2pManager, channel)

        wifiP2pManager.requestGroupInfo(channel) { p0 ->

            if (p0 != null) {

                Toast.makeText(requireContext(), "channel is previous group", Toast.LENGTH_SHORT)
                    .show()
                Toast.makeText(requireContext(), "removing group", Toast.LENGTH_SHORT).show()

                wifiP2pManager.removeGroup(channel,object :WifiP2pManager.ActionListener{

                    override fun onSuccess() {
                        Toast.makeText(requireContext(), "remove Group success", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(p0: Int) {
                        Toast.makeText(requireContext(), "remove group failure:$p0", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_wait_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrImage = view.findViewById(R.id.fragment_wait_device_qr_image)

        Toast.makeText(requireContext(),arguments?.getStringArrayList(TransferFragment.FILE_PATHS)?.size?.toString(),Toast.LENGTH_SHORT).show()

        signalRipple = view.findViewById(R.id.fragment_wait_device_signal_ripple)

        var bitMatrix: BitMatrix? = null

        val width = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                224f,
                resources.displayMetrics
            )
        )
        val height = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                224f,
                resources.displayMetrics
            )
        )


        try {
            bitMatrix =
                MultiFormatWriter().encode("Whyred", BarcodeFormat.QR_CODE, width, height, null)
        } catch (w: WriterException) {

        }

        val w = bitMatrix?.width
        val h = bitMatrix?.height

        val pixels = IntArray(w!! * h!!)

        for (y in 0 until h) {

            val offset = y * w

            for (x in 0 until w) {
                pixels[offset + x] = if (bitMatrix!!.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        qrBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        qrBitmap.setPixels(pixels, 0, width, 0, 0, w, h)

        qrImage.setImageBitmap(qrBitmap)

        //start discovering devices
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                Toast.makeText(requireContext(), "discover success", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(p0: Int) {
                Toast.makeText(requireContext(), "discover failed:$p0", Toast.LENGTH_SHORT).show()

            }
        })
    }

    override fun onResume() {
        super.onResume()

        requireContext().registerReceiver(p2pBroadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()

        requireContext().unregisterReceiver(p2pBroadcastReceiver)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return arrayListOf()
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onStateChanged() {

    }

    override fun onThisDevice(wifiP2pDevice: WifiP2pDevice) {
        Toast.makeText(requireContext(), wifiP2pDevice.deviceName, Toast.LENGTH_SHORT).show()
    }

    override fun onDeviceList(wifiP2pDeviceList: WifiP2pDeviceList) {

    }

    override fun onConnection(wifiP2pInfo: WifiP2pInfo) {

        if (wifiP2pInfo.groupFormed) {

            var transferFragment = requireActivity().supportFragmentManager.findFragmentByTag(TransferFragment.TAG)

            if (transferFragment == null){
                transferFragment = TransferFragment().also {
                    it.arguments = Bundle().apply {

                        putParcelable(TransferFragment.P2P_INFO,wifiP2pInfo)
                        putStringArrayList(TransferFragment.FILE_PATHS,arguments?.getStringArrayList(TransferFragment.FILE_PATHS))
                        putInt(TransferFragment.TRANSFER_TYPE,TransferFragment.SEND)

                    }
                }
            }

            requireActivity().supportFragmentManager.commit {

                setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim
                )
                addToBackStack(null)
                replace(R.id.activity_send_navigator,transferFragment,TransferFragment.TAG)
            }
        }
    }

    override fun onDiscoveryStarted() {
        signalRipple.startAnimation()
    }

    override fun onDiscoveryStopped() {
        signalRipple.stopAnimation()
    }

    override fun onBackPressed(): Boolean {

        signalRipple.stopAnimation()
        wifiP2pManager.stopPeerDiscovery(channel,null)

        return true
    }

    override fun loadImage(imageView: ImageView, position: Int) {

    }

    companion object {

        @JvmField
        val TAG: String = WaitDeviceFragment::class.simpleName!!

        val intentFilter = IntentFilter().apply {

            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }
    }
}