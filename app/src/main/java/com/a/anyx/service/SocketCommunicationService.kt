package com.a.anyx.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.a.anyx.interfaces.ConnectionListener
import com.a.anyx.interfaces.TransferEventListener
import com.a.anyx.util.XFer
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.Executors

class SocketCommunicationService : Service() {

    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null

    private var transferEventListener:TransferEventListener? = null
    private var connectionListener: ConnectionListener? = null

    private lateinit var xFer:XFer

    inner class LocalBinder:Binder(){

        fun getService():SocketCommunicationService{
            return this@SocketCommunicationService
        }
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder = binder

    fun createServer(){

        Executors.newSingleThreadExecutor().execute {

            serverSocket = ServerSocket(8888)
            serverSocket?.soTimeout = 15000
            serverSocket?.reuseAddress = true

            socket = serverSocket?.accept()


            xFer = XFer(socket!!,transferEventListener,connectionListener)
            connectionListener?.onSocketConnection()
        }
    }

    fun createClient(host:String){

        Executors.newSingleThreadExecutor().execute {

            while (true){

                try {

                    socket = Socket(host, PORT)
                    break
                }catch (s:SocketException){

                    Thread.sleep(1000)
                }catch (i:IOException){
                    break
                }
            }

            xFer = XFer(socket!!,transferEventListener,connectionListener)
            connectionListener?.onSocketConnection()

        }

    }

    fun send(filesArray: ArrayList<String>){

        socket?.also {

                xFer.send(filesArray)
        }
    }

    fun receive(){

        socket?.also {

                xFer.receive()
        }
    }

    fun setTransferEventListener(listener: TransferEventListener){

        transferEventListener = listener
    }

    fun removeTransferEventListener(){

        if (transferEventListener != null)
            transferEventListener = null
    }

    fun setConnectionListener(c:ConnectionListener){
        connectionListener = c
    }

    fun removeConnectionListener(){

        if (connectionListener != null){
            connectionListener = null
        }
    }

    fun disconnect(){

        if (socket != null){
            socket?.close()
        }

        if (serverSocket != null){
            serverSocket?.close()
        }

    }

    fun cancelSendAt(position:Int){

        xFer.cancelIndex = position
    }

    companion object{

        const val PORT = 8888
    }
}