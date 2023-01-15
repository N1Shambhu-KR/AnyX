package com.a.anyx.fragment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Size
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.BaseFragment
import com.a.anyx.fragment.FileFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import java.util.concurrent.Executors

class ContentDataAdapter(private val fragment: BaseFragment, private var data: ArrayList<ContentData>, private val viewType: Int):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val LINEAR_VIEW_TYPE = 1
        const val GRID_VIEW_TYPE = 2
    }

    private var lastSelected = -1

    private val adapterSelection = AdapterSelection(false)

    private var listener: OnRecyclerViewItemClick? = null

    fun setRecyclerViewItemClickListener(_listener: OnRecyclerViewItemClick){
        listener = _listener
    }

    fun setData(_data: ArrayList<ContentData>){

        data = _data
        notifyDataSetChanged()
    }

    fun setAdapterSelection(arrayList: ArrayList<Long>){
        adapterSelection.setNew(arrayList)
    }

    fun getAdapterSelection():ArrayList<Long> = adapterSelection.selection

    inner class LinearViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val thumb = itemView.findViewById<ImageView>(R.id.linear_content_data_holder_icon)
        val name = itemView.findViewById<TextView>(R.id.linear_content_data_holder_name)
        val size = itemView.findViewById<TextView>(R.id.linear_content_data_holder_size)

        val checker = itemView.findViewById<CheckBox>(R.id.checker)

        val selection_overlay = itemView.findViewById<FrameLayout>(R.id.selection_overlay)

        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(position: Int){

            thumb.also {
                fragment.loadImage(thumb,position)

            }

            selection_overlay.visibility = if (adapterSelection.getSelected(getItemId(position))) View.VISIBLE else View.GONE

            checker.isChecked = adapterSelection.getSelected(getItemId(position))

            itemView.setOnClickListener {

                listener?.onItemClick(position,it)
            }

            checker.setOnClickListener {

                notifyItemChanged(lastSelected)
                adapterSelection.setSelection(getItemId(position))
                lastSelected = position
                notifyItemChanged(lastSelected)
                selection_overlay.visibility = if (adapterSelection.getSelected(getItemId(position))) View.VISIBLE else View.GONE

                listener?.onItemClick(position,checker)
            }

            name.setText(data[position].name).apply {

            }
            size.text = data[position].length.toString()

         }
    }

    inner class GridViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

         val thumb = itemView.findViewById<ImageView>(R.id.grid_content_data_holder_icon)

        val selection_overlay = itemView.findViewById<FrameLayout>(R.id.selection_overlay)

        val checker = itemView.findViewById<CheckBox>(R.id.checker)

        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(position: Int){

            thumb.also {
                fragment.loadImage(thumb,position)

            }

            selection_overlay.visibility = if (adapterSelection.getSelected(getItemId(position))) View.VISIBLE else View.GONE

            checker.isChecked = adapterSelection.getSelected(getItemId(position))

            itemView.setOnClickListener {

                listener?.onItemClick(position,it)
            }

            checker.setOnClickListener {

                notifyItemChanged(lastSelected)
                adapterSelection.setSelection(getItemId(position))
                lastSelected = position
                notifyItemChanged(lastSelected)
                selection_overlay.visibility = if (adapterSelection.getSelected(getItemId(position))) View.VISIBLE else View.GONE

                listener?.onItemClick(position,checker)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == LINEAR_VIEW_TYPE){
            return LinearViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.linear_content_data_holder,parent,false))

        }

            return GridViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_content_data_holder,parent,false))

    }

    override fun getItemCount(): Int {
        return data.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (viewType == LINEAR_VIEW_TYPE){

            (holder as LinearViewHolder).bind(position)
        }else{
            (holder as GridViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemId(position: Int): Long {
        return data[position].hashCode().toLong()
    }

}