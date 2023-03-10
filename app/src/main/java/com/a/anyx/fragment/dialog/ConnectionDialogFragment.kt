package com.a.anyx.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.a.anyx.R

class ConnectionDialogFragment():DialogFragment() {

    private lateinit var device:String

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(

            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        device = arguments?.getString(DEVICE_NAME,"IDK")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_connection_dialog,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.fragment_connection_device_name).apply {

            text = device
        }

    }

    companion object{

        const val DEVICE_NAME = "device_name"

        @JvmField val TAG:String = ConnectionDialogFragment::class.simpleName!!
    }
}