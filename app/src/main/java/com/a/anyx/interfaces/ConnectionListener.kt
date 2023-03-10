package com.a.anyx.interfaces

import java.net.Socket

interface ConnectionListener{

    fun onSocketConnection()

    fun onSocketClose(ioError:Boolean)
}
