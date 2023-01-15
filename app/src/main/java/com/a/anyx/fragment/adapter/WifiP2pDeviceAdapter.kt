package com.a.anyx.fragment.adapter

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.interfaces.OnRecyclerViewItemClick

class WifiP2pDeviceAdapter(private val applicationContext:Context,private val items:Collection<WifiP2pDevice>):RecyclerView.Adapter<WifiP2pDeviceAdapter.ViewHolder>() {

    private val adapterSelection = AdapterSelection(true)
    private var listener: OnRecyclerViewItemClick? = null

    fun getAdapterSelection():ArrayList<Long> = adapterSelection.selection

    fun setRecyclerViewItemClickListener(_listener:OnRecyclerViewItemClick){
        listener = _listener
    }

    fun removeRecyclerViewItemClickListener(_listener:OnRecyclerViewItemClick){

        if (_listener == listener){
            listener = null
        }
    }

    private var selectedPosition:Int = -1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items.toList()[position]

        holder.bind(item,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.wifi_p2p_device_holder,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items.toList()[position].hashCode().toLong()
    }

    inner class ViewHolder(private val itemView: View):RecyclerView.ViewHolder(itemView){

        private val deviceName = itemView.findViewById<TextView>(R.id.wifi_p2p_device_holder_device_name)
        private val deviceAddress = itemView.findViewById<TextView>(R.id.wifi_p2p_device_holder_device_address)

        fun bind( device: WifiP2pDevice,position: Int){

            deviceName.text = device.deviceName
            deviceAddress.text = device.deviceAddress

            itemView.background = if (selectedPosition == position) ContextCompat.getDrawable(applicationContext,R.drawable.bg_wifi_p2p_device_layout) else null

            itemView.setOnClickListener {

                notifyItemChanged(selectedPosition)
                adapterSelection.setSelection(getItemId(position))
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                itemView.background = if (adapterSelection.getSelected(getItemId(position))) ContextCompat.getDrawable(applicationContext,R.drawable.bg_wifi_p2p_device_layout) else null

                listener?.onItemClick(position,itemView)
            }
        }
    }

    fun getSelectedItems():ArrayList<Any?>{

        return arrayListOf<Any?>().apply {

            for (i in items){

                if (adapterSelection.selection.contains(i.hashCode().toLong()))
                    add(i)
            }
        }
    }
}