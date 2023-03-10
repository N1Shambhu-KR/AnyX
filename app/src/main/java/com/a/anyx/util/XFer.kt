package com.a.anyx.util

import android.os.Environment
import androidx.annotation.Nullable
import com.a.anyx.interfaces.ConnectionListener
import com.a.anyx.interfaces.TransferEventListener
import java.io.*
import java.net.Socket
import java.util.concurrent.Executors
import kotlin.math.min

class XFer(
    private val socket: Socket,
    @Nullable private val transferEventListener: TransferEventListener?,
    @Nullable private val connectionListener: ConnectionListener?
) {

    private val dataOut: DataOutputStream = DataOutputStream(socket.getOutputStream())
    private val dataIn: DataInputStream = DataInputStream(socket.getInputStream())

    var cancelIndex: Int = -1

    fun send(filePaths: ArrayList<String>) {

        Executors.newSingleThreadExecutor().execute {

            //sot
            transferEventListener?.onTransferSessionStart()

            /*tries to send files and throws IO exception when the socket is disconnected
            * */
            try {

                //writes the initial files array size
                dataOut.writeInt(filePaths.size)

                //iterates through thr files array
                for (i in 0 until filePaths.size) {

                    //get the file associated with the file path
                    val file = File(filePaths[i])

                    //checks if file exists
                    if (file.exists()) {

                        transferEventListener?.onStartTransfer(file, file.length())

                        //writes file name and file length to the stream
                        dataOut.writeUTF(file.name)
                        dataOut.writeLong(file.length())

                        val buffer = ByteArray(4 * 1024)
                        var bytesRead: Int = 0

                        FileInputStream(file).use { fileInputStream ->

                            while (true) {

                                //read the buffer from the file input stream
                                bytesRead = fileInputStream.read(buffer)

                                //if the file has not eof
                                if (bytesRead > 0) {
                                    dataOut.write(buffer, 0, bytesRead)

                                    transferEventListener?.onBytesTransferred(file, bytesRead)
                                } else {

                                    break
                                }


                            }

                            fileInputStream.close()
                            transferEventListener?.onFinishTransfer(file)
                        }

                    }
                }

            } catch (e: IOException) {

                /*notify the host about connection loss or error when writing to the stream
                 */
                transferEventListener?.onSocketIOError()

                connectionListener?.onSocketClose(true)
            }

            //eot
            transferEventListener?.onTransferSessionEnd()
        }

    }

    fun receive() {

        Executors.newSingleThreadExecutor().execute {

            /*when receiving files set the initial disconnected to false
            */
            var disconnected = false

            transferEventListener?.onTransferSessionStart()

            //reads the total files array being received
            val fileCount = dataIn.readInt()

            //iterate for the incoming stream bytes
            for (i in 0 until fileCount) {

                val fileName = dataIn.readUTF()
                var fileLength = dataIn.readLong()

                val buffer = ByteArray(4 * 1024)
                var bytesReceived: Int

                val outFile = File("${Environment.getExternalStorageDirectory()}/$fileName")

                transferEventListener?.onStartReceive(outFile, fileLength)

                /*looks through the input stream and reads the input's byte array
                * and writes the byte array to the output stream associated with outFile if the cancelIndex is not current loop index e.g i
                */
                val fileOutputStream = FileOutputStream(outFile)

                //reads from the input stream until fileLength or remaining fileLength is > 0
                while (true) {

                    if (fileLength > 0) {

                        try {
                            //reads the input stream for the remaining bytes
                            bytesReceived =
                                dataIn.read(buffer, 0, min(buffer.size, fileLength.toInt()))

                            fileOutputStream.write(buffer, 0, bytesReceived)
                            fileLength -= bytesReceived

                            transferEventListener?.onBytesReceived(outFile, bytesReceived)
                        }catch (e:IOException){

                            /*if the socket gets disconnected then the bytesReceived will be -1
                             *notify the host about connection loss or error when reading the stream
                             */
                            transferEventListener?.onSocketIOError()

                            connectionListener?.onSocketClose(true)
                            disconnected = true
                            break
                        }

                    } else {

                        break
                    }

                }

                fileOutputStream.flush()

                fileOutputStream.close()


                if (disconnected) {
                    break
                } else {
                    transferEventListener?.onFinishReceive(outFile)
                }
            }

            transferEventListener?.onTransferSessionEnd()
        }


    }

}