package com.a.anyx.fragment

import android.annotation.SuppressLint
import android.content.*
import android.net.wifi.p2p.*
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.a.anyx.P2pBroadcastReceiver
import com.a.anyx.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class ConnectDeviceFragment(): BaseFragment() {

    private lateinit var devices:ArrayList<WifiP2pDevice>
    private lateinit var deviceNames:ArrayList<String>
    private lateinit var deviceAdapter: ArrayAdapter<String>

    private lateinit var searchingIndic:LinearProgressIndicator
    private lateinit var deviceListView: ListView

    private lateinit var p2pBroadcastReceiver: P2pBroadcastReceiver
    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel


    companion object{

        @JvmField val TAG:String = ConnectDeviceFragment::class.simpleName!!

        const val FILE_PATHS = "file_paths"
        const val WIFI_P2P_INFO = "p2p_info"

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        wifiP2pManager = requireActivity().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(requireContext(), Looper.getMainLooper(),null)

        wifiP2pManager.requestGroupInfo(channel,object :WifiP2pManager.GroupInfoListener{

            override fun onGroupInfoAvailable(p0: WifiP2pGroup?) {

                if (p0 != null){

                    wifiP2pManager.removeGroup(channel,object :WifiP2pManager.ActionListener{

                        override fun onSuccess() {
                            wifiP2pManager.discoverPeers(channel,null)
                        }

                        override fun onFailure(p0: Int) {

                        }
                    })
                }else{
                    wifiP2pManager.discoverPeers(channel,null)
                }
            }
        })

        p2pBroadcastReceiver = P2pBroadcastReceiver(requireActivity(),wifiP2pManager, channel)

        devices = ArrayList()
        deviceNames = ArrayList<String>()
        deviceAdapter = ArrayAdapter(requireActivity(),android.R.layout.simple_list_item_1,deviceNames)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_to_owner, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*deviceListView =  view.findViewById<ListView>(R.id.fragment_wait_device_searching_result).apply {

            adapter = deviceAdapter

            setOnItemClickListener { parent, view, position, id ->

                val config = WifiP2pConfig()
                config.deviceAddress = devices[position].deviceAddress

                wifiP2pManager.connect(channel,config,object :WifiP2pManager.ActionListener{

                    override fun onFailure(p0: Int) {

                    }

                    override fun onSuccess() {
                        Toast.makeText(requireContext(),"conn started",Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        deviceNames.apply {
            add("hjgv")
            add("ggggfg")
            add("kjhbhjhj")
            add("gfgcfgcgfchgc")
            add("bjbbjhbb")
            add("jgvhgggfcc")
            add("vfgcgf")
            add("ghvghcgc")
            add("ggfdxfdxfx")
            add("gvghvhvchchg")
            add("fcfgcncfc")
            add("hcfccgfchch")
            add("hjvghvfcf")
            add("vjcdfxfchmc")
            add("ffcfgdxfdxd")
            add("gcfcfcfgxxxf")
            add("gfgfcfcfxfxf")
            add("xxffzfzdx")
        }

        deviceAdapter.notifyDataSetChanged()

        searchingIndic = view.findViewById(R.id.fragment_wait_device_searching_indic)*/
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(p2pBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(p2pBroadcastReceiver, intentFilter)
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return null
    }

    override fun getTAG(): String? {
        return TAG
    }

    fun onDeviceState(p2PEnabled:Boolean){

    }

    fun onDeviceList(deviceList: WifiP2pDeviceList){

        /*devices.clear()
        devices.addAll(deviceList.deviceList)

        deviceNames.clear()

        for (d in devices){
            deviceNames.add(d.deviceName)
        }

        deviceAdapter.notifyDataSetChanged()*/
    }

    fun onThisDevice(device: WifiP2pDevice){

    }

    fun onConnection(info: WifiP2pInfo) {


    }

    fun onDiscoveryStart(){
        //searchingIndic.visibility = View.VISIBLE
    }

    fun onDiscoveryStop(){
        //searchingIndic.visibility = View.GONE
    }

}