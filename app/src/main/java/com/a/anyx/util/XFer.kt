package com.a.anyx.util

import android.os.Environment
import androidx.annotation.Nullable
import com.a.anyx.R
import com.a.anyx.interfaces.TransferEventListener
import java.io.*

class XFer(private val output:OutputStream,
           private val input:InputStream,
           @Nullable private val transferEventListener: TransferEventListener?) {

    private val dataOut:DataOutputStream = DataOutputStream(output)
    private val dataIn:DataInputStream = DataInputStream(input)

    fun send(filePaths:ArrayList<String>){

        dataOut.writeInt(filePaths.size)

        for (path in filePaths){

            val f = File(path)

            val buffer = ByteArray(4*1024)
            var readLength = 0

            dataOut.writeUTF(f.name)
            dataOut.writeLong(f.length())

            transferEventListener?.onStartTransfer(f)

            val fileInputStream = FileInputStream(f)

            while (true){

                readLength = fileInputStream.read(buffer)

                if (readLength > 0) {

                    dataOut.write(buffer,0,readLength)
                    transferEventListener?.onBytesTransferred(f,readLength)
                } else{
                    break
                }

            }

            fileInputStream.close()

            transferEventListener?.onFinishTransfer(f)

        }

        dataOut.close()
        dataIn.close()

    }

    fun receive(){

        val fileCount = dataIn.readInt()

        for (i in 0 until fileCount){

            val buffer = ByteArray(4*1024)
            var readLength = 0

            val fileName = dataIn.readUTF()
            var fileLength = dataIn.readLong()

            val parent = File("${Environment.getExternalStorageDirectory()}/${R.string.app_name}")

            if (!parent.exists()){
                parent.mkdir()
            }

            val f = File("${Environment.getExternalStorageDirectory()}/$fileName")

            transferEventListener?.onStartReceive(f)

            val fileOutputStream = FileOutputStream(f)

            while (fileLength >0){

                readLength = dataIn.read(buffer,0,Math.min(buffer.size,fileLength.toInt()))

                if (readLength >0){
                    fileOutputStream.write(buffer,0,readLength)
                    transferEventListener?.onBytesReceived(f,readLength)
                }
            }

            fileOutputStream.close()

            transferEventListener?.onFinishReceive(f)
        }

        dataIn.close()
        dataOut.close()

    }
}