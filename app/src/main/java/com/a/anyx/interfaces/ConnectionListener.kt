package com.a.anyx.interfaces

import java.net.Socket

interface ConnectionListener{

    fun onConnection()

    fun onDisconnect()
}
