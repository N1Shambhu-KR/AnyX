package com.a.anyx.interfaces

import java.io.File

interface TransferEventListener {

    fun onTransferSessionStart()

    fun onStartTransfer(file: File, maxLength: Long)
    fun onBytesTransferred(file: File, bytesTransferred: Int)
    fun onFinishTransfer(file: File)
    fun onCancel(file: File)
    fun onStartReceive(file: File, maxLength: Long)
    fun onBytesReceived(file: File, bytesReceived: Int)
    fun onFinishReceive(file: File)
    fun onTransferSessionEnd()
    fun onSocketIOError()

}
