package com.a.anyx.fragment.adapter

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.FileObject
import com.a.anyx.content.FileObject.TransferStatus
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.base.TransferBaseFragment
import com.a.anyx.util.DataUtils
import com.a.anyx.util.TimeUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.File
import java.net.URLConnection
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class SendReceiveAdapter(private val base: BaseFragment, val handler: Handler) :
    RecyclerView.Adapter<SendReceiveAdapter.ViewHolder>() {

    val fileObjects: MutableList<FileObject> = mutableListOf()

    fun getItems() = fileObjects

    private val fileNameMap by lazy {
        URLConnection.getFileNameMap()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val typeImage: ImageView =
            itemView.findViewById(R.id.send_receive_layout_type_image)
        private val fileName: TextView = itemView.findViewById(R.id.send_receive_layout_file_name)
        private val fileLength: TextView =
            itemView.findViewById(R.id.send_receive_layout_file_length)
        private val transferProgress: LinearProgressIndicator =
            itemView.findViewById(R.id.send_receive_layout_transfer_progress)

        private val statusText: TextView =
            itemView.findViewById(R.id.send_receive_layout_status_text)

        private val actionComplete: Button =
            itemView.findViewById(R.id.send_receive_layout_action_complete)

        private val trafficField:TextView = itemView.findViewById(R.id.send_receive_layout_traffic)

        fun bind(fileObject: FileObject) {

            if (fileObject.type == SEND) {
                base.loadImage(typeImage,fileObjects.indexOf(fileObject))
            }else{

                actionComplete.isEnabled = false
            }

            fileName.text = fileObject.name

            fileLength.text = DataUtils.bytesToReadableFormat(fileObject.maxLength)

            actionComplete.setOnClickListener {

                createFileObjectAction(fileObject)
            }

            val errorString = base.requireActivity().getString(R.string.error)

            val openString = base.requireActivity().getString(R.string.open)

            //lookup for the current fileObject in progress
            Executors.newSingleThreadExecutor().execute {

                //loop the current fileObjet until fileObject status END || ERROR || CANCELLED
                while (true) {

                       val totalConcatenatedLengthString = fileObject.currentLength.toString().plus("/").plus(fileObject.maxLength.toString())

                        //if the current fileObject's status is XFer error
                        if (fileObject.transferStatus == TransferStatus.ERROR) {

                            handler.post {
                                transferProgress.progress =
                                    (fileObject.currentLength.toFloat() / fileObject.maxLength.toFloat() * 100).roundToInt()

                                (actionComplete as MaterialButton).also {
                                    it.text = errorString
                                    it.icon = null

                                    it.isEnabled = false
                                }

                                statusText.text = errorString

                            }

                            break

                        }

                        if (fileObject.transferStatus == TransferStatus.CANCELLED){

                            fileObjects.indexOf(fileObject).also {

                                fileObjects.removeAt(it)

                                handler.post {

                                    actionComplete.isEnabled = false
                                    notifyItemRemoved(it)
                                }
                            }

                            break
                        }

                        //if current fileObject has completed e.g sent or received
                        if (fileObject.transferStatus == TransferStatus.END) {

                            if (fileObject.type == SEND) {

                                //file sent

                                handler.post {
                                    transferProgress.progress =
                                        (fileObject.currentLength.toFloat() / fileObject.maxLength.toFloat() * 100).roundToInt()

                                    actionComplete.isEnabled = false
                                    statusText.setText(R.string.sent)
                                }

                            } else {

                                //file received

                                handler.post {
                                    transferProgress.progress =
                                        (fileObject.currentLength.toFloat() / fileObject.maxLength.toFloat() * 100).roundToInt()

                                    (actionComplete as MaterialButton).also {
                                        it.isEnabled = true
                                        it.text = openString
                                        it.icon = null
                                    }

                                    statusText.setText(R.string.received)
                                }

                                base.loadImage(typeImage,fileObjects.indexOf(fileObject))

                            }

                            break
                        }

                        //if the fileObject's status is in progress
                        if (fileObject.transferStatus == TransferStatus.IN_PROGRESS) {

                            val beforeSleep = fileObject.currentLength

                            Thread.sleep(1000)

                            val traffic = fileObject.currentLength - beforeSleep

                            handler.post {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    transferProgress.setProgress((fileObject.currentLength.toFloat() / fileObject.maxLength.toFloat() * 100).roundToInt(),true)
                                }else{
                                    transferProgress.progress = (fileObject.currentLength.toFloat() / fileObject.maxLength.toFloat() * 100).roundToInt()
                                }

                                //fileLength.text = fileObject.currentLength.toString().plus("/").plus(fileObject.maxLength.toString())

                                trafficField.text = DataUtils.bytesToReadableFormat(traffic).plus("/s")

                                statusText.setText(R.string.in_progress)
                            }

                        }

                }
            }
        }

        private fun createFileObjectAction(fo: FileObject) {

            when(fo.transferStatus){

                TransferStatus.END->{

                    var contentUri: Uri? = null

                    val fileCursor = base.requireContext().contentResolver.query(
                        MediaStore.Files.getContentUri("external"),
                        arrayOf(MediaStore.Files.FileColumns._ID),
                        MediaStore.Files.FileColumns.DATA + "=?",
                        arrayOf(fo.path), null
                    )

                    try {

                        fileCursor?.apply {

                            moveToFirst()

                            val id =
                                getLong(fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                            contentUri = ContentUris.withAppendedId(
                                MediaStore.Files.getContentUri("external"), id
                            )

                            close()
                        }


                    } catch (e: Exception) {

                        Log.d("SendReceiveAdapter", e.message!!)
                    }

                    //Toast.makeText(base.requireContext(),contentUri.toString(),Toast.LENGTH_SHORT).show()

                    contentUri?.also {

                        val openIntent = Intent().apply {

                            action = Intent.ACTION_VIEW
                            setDataAndType(it,fileNameMap.getContentTypeFor(fo.name))
                            putExtra(Intent.EXTRA_STREAM, it)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        base.requireActivity().startActivity(openIntent)
                    }

                }

                TransferStatus.IN_PROGRESS->{

                    if (base is TransferBaseFragment)
                    {
                        base.cancelAt(fileObjects.indexOf(fo))
                        actionComplete.isEnabled = false
                    }
                }

                else->{}
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val finalFileObject = fileObjects[position]
        holder.bind(finalFileObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == SEND) {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_item_layout, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.receive_item_layout, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return fileObjects[position].type
    }

    override fun getItemCount(): Int {
        return fileObjects.size
    }


    fun add(file: File, type: Int, maxLength: Long) {

        fileObjects.add(
            FileObject(
                TransferStatus.START,
                file.absolutePath,
                file.name,
                type,
                0,
                maxLength
            )
        )

        (fileObjects.size - 1).also {

            handler.post {
                notifyItemInserted(it)

            }
        }

    }

    fun update(fileName: String, bytes: Long) {

        for (f in fileObjects) {

            if (f.name == fileName) {

                f.currentLength += bytes
                f.transferStatus = TransferStatus.IN_PROGRESS
                break
            }
        }
    }

    fun finish(fileName: String) {

        for (f in fileObjects) {

            if (f.name == fileName) {

                f.transferStatus = TransferStatus.END
                break
            }
        }
    }

    fun cancel(fileName: String){

        for (f in fileObjects) {

            if (f.name == fileName) {

                f.transferStatus = TransferStatus.CANCELLED
                break
            }
        }
    }

    fun transferError() {

        fileObjects[fileObjects.size - 1].transferStatus = TransferStatus.ERROR
    }

    companion object {

        const val SEND = 1
        const val RECEIVE = 2
    }
}