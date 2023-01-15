package com.a.anyx.interfaces

import java.io.File

interface TransferEventListener {

    fun onStartTransfer(file: File)

    fun onFinishTransfer(file: File)

    fun onBytesTransferred(file: File, bytesTransferred: Int)

    fun onBytesReceived(file: File, bytesReceived: Int)

    fun onFinishReceive(file: File)

    fun onStartReceive(file: File)

}
