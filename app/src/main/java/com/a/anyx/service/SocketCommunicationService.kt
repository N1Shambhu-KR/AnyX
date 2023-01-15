package com.a.anyx.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.a.anyx.interfaces.ConnectionListener
import com.a.anyx.interfaces.TransferEventListener
import com.a.anyx.util.XFer
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.Executors

class SocketCommunicationService : Service() {

    private lateinit var serverSocket: ServerSocket
    private lateinit var socket: Socket

    private var transferEventListener:TransferEventListener? = null
    private var connectionListener: ConnectionListener? = null

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
            serverSocket.soTimeout = 15000
            serverSocket.reuseAddress = true

            socket = serverSocket.accept()

                connectionListener?.onConnection()
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

                connectionListener?.onConnection()

        }

    }

    fun send(filesArray: ArrayList<String>){

        socket.also {

            Executors.newSingleThreadExecutor().execute {
                val transfer = XFer(it.getOutputStream(),it.getInputStream(),transferEventListener)
                transfer.send(filesArray)
            }

        }
    }

    fun receive(){

        socket.also {

            Executors.newSingleThreadExecutor().execute {
                val transfer = XFer(it.getOutputStream(),it.getInputStream(),transferEventListener)
                transfer.receive()
            }

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

    companion object{

        const val PORT = 8888
    }
}