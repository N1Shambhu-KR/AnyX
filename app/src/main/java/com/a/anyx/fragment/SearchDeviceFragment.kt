package com.a.anyx.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.P2pBroadcastReceiver
import com.a.anyx.R
import com.a.anyx.fragment.adapter.WifiP2pDeviceAdapter
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.dialog.ConnectionDialogFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import com.a.anyx.interfaces.OnWiFiP2pChanged
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator


@SuppressWarnings("notifyDataSetChanged", "MissingPermission")
class SearchDeviceFragment : BaseFragment(), OnWiFiP2pChanged, OnRecyclerViewItemClick {

    private lateinit var deviceRecycler: RecyclerView
    private lateinit var discoveringView: LinearProgressIndicator
    private lateinit var tapToReceive: TextView

    private lateinit var scanDevices: Button
    private lateinit var connectDevice: Button

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var broadcastReceiver: P2pBroadcastReceiver

    private lateinit var devices: MutableList<WifiP2pDevice>
    private lateinit var wifiP2pDeviceAdapter: WifiP2pDeviceAdapter

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create a wifiP2pManager object from system service
        wifiP2pManager =
            requireContext().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

        //initialization of manager returns a WifiP2pManager.Channel object which we can used to determine channel status
        channel = wifiP2pManager.initialize(requireContext(), Looper.getMainLooper()) {}

        //register a broadcast for receiving intents when there is change in the framework
        broadcastReceiver = P2pBroadcastReceiver(this, wifiP2pManager, channel)

        wifiP2pManager.requestGroupInfo(channel) {

            if (it != null) {

                Toast.makeText(requireContext(), "channel is previous group", Toast.LENGTH_SHORT)
                    .show()
                Toast.makeText(requireContext(), "removing group", Toast.LENGTH_SHORT).show()

                wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {

                    override fun onSuccess() {
                        Toast.makeText(requireContext(), "remove Group success", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onFailure(p0: Int) {
                        Toast.makeText(
                            requireContext(),
                            "remove group failure:$p0",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })


            }
        }

        devices = mutableListOf()
        wifiP2pDeviceAdapter = WifiP2pDeviceAdapter(requireContext(), devices)
        wifiP2pDeviceAdapter.setRecyclerViewItemClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {

            setNavigationOnClickListener {

                requireActivity().onBackPressed()
            }

        }

        deviceRecycler =
            view.findViewById<RecyclerView?>(R.id.fragment_search_device_recycler).also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = wifiP2pDeviceAdapter

            }

        discoveringView = view.findViewById(R.id.fragment_search_device_discover)

        scanDevices = view.findViewById<Button?>(R.id.fragment_search_device_scan).also {

            it.setOnClickListener {

                devices.clear()
                wifiP2pDeviceAdapter.notifyDataSetChanged()

                wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                    override fun onSuccess() {
                        Toast.makeText(requireContext(), "discover success", Toast.LENGTH_SHORT)
                            .show()

                    }

                    override fun onFailure(p0: Int) {
                        Toast.makeText(requireContext(), "discover failed:$p0", Toast.LENGTH_SHORT)
                            .show()

                    }
                })
            }
        }

        connectDevice = view.findViewById<Button?>(R.id.fragment_search_device_connect).also {

            it.setOnClickListener {

                connect(wifiP2pDeviceAdapter.getSelectedItems()[0] as WifiP2pDevice)
            }
        }

        tapToReceive = view.findViewById(R.id.fragment_search_device_tap_to_receive)

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

    override fun onItemClick(position: Int, view: View) {

        if (wifiP2pDeviceAdapter.getAdapterSelection().size > 0) {
            connectDevice.isEnabled = true
        }

    }


    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()

        requireContext().unregisterReceiver(broadcastReceiver)
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

        Toast.makeText(requireContext(), "device list changed", Toast.LENGTH_SHORT).show()

        devices.clear()
        devices.addAll(wifiP2pDeviceList.deviceList)

        if (devices.size > 0 && tapToReceive.visibility == View.VISIBLE) {
            tapToReceive.visibility = View.GONE
        }

        wifiP2pDeviceAdapter.notifyDataSetChanged()
    }

    override fun onConnection(wifiP2pInfo: WifiP2pInfo) {

        if (wifiP2pInfo.groupFormed) {

            childFragmentManager.findFragmentByTag(ConnectionDialogFragment.TAG)?.also {

                (it as ConnectionDialogFragment).dismiss()
            }

            Toast.makeText(requireContext(), wifiP2pInfo.toString(), Toast.LENGTH_SHORT).show()

            var transferFragment =
                requireActivity().supportFragmentManager.findFragmentByTag(TransferFragment.TAG)

            if (transferFragment == null) {
                transferFragment = TransferFragment().also {
                    it.arguments = Bundle().apply {

                        putParcelable(TransferFragment.P2P_INFO, wifiP2pInfo)
                        putInt(TransferFragment.TRANSFER_TYPE, TransferFragment.RECEIVE)

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
                replace(R.id.activity_receive_navigator, transferFragment, TransferFragment.TAG)
            }
        }
    }

    override fun onDiscoveryStarted() {
        discoveringView.visibility = View.VISIBLE
    }

    override fun onDiscoveryStopped() {
        discoveringView.visibility = View.GONE
    }

    override fun onBackPressed(): Boolean {

        return true
    }

    override fun loadImage(imageView: ImageView, position: Int) {

    }

    private fun connect(device: WifiP2pDevice) {

        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.wps.setup = WpsInfo.PBC

        wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onFailure(p0: Int) {
                Toast.makeText(requireContext(), "connect failure:$p0", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {

                val dialog = ConnectionDialogFragment()
                dialog.isCancelable = false
                dialog.arguments = Bundle().apply {

                    putString(ConnectionDialogFragment.DEVICE_NAME, device.deviceName)
                }

                dialog.show(childFragmentManager, ConnectionDialogFragment.TAG)
            }
        })
    }


    companion object {
        @JvmField
        val TAG: String = SearchDeviceFragment::class.simpleName!!

        val intentFilter = IntentFilter().apply {

            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }
    }
}

