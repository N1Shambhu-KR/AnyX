package com.a.anyx.fragment

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.fragment.adapter.SendReceiveAdapter
import com.a.anyx.fragment.base.TransferBaseFragment
import com.a.anyx.interfaces.ConnectionListener
import com.a.anyx.interfaces.TransferEventListener
import com.a.anyx.service.SocketCommunicationService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.net.URLConnection
import java.util.concurrent.Executors


class TransferFragment : TransferBaseFragment(), TransferEventListener, ConnectionListener {

    private lateinit var socketCommunicationService: SocketCommunicationService

    private val serviceConnectionListener = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as SocketCommunicationService.LocalBinder

            socketCommunicationService = binder.getService()

            socketCommunicationService.setConnectionListener(this@TransferFragment)
            socketCommunicationService.setTransferEventListener(this@TransferFragment)

            val p2pInfo = arguments?.getParcelable<WifiP2pInfo>(P2P_INFO)!!

            if (p2pInfo.isGroupOwner) {

                socketCommunicationService.createServer()
            } else {
                socketCommunicationService.createClient(p2pInfo.groupOwnerAddress.hostAddress!!)
            }


        }

        override fun onServiceDisconnected(name: ComponentName?) {

            socketCommunicationService.removeConnectionListener()
            socketCommunicationService.removeTransferEventListener()
        }
    }

    private lateinit var handler: Handler

    private lateinit var transferRecycler: RecyclerView
    private lateinit var sendReceiveAdapter: SendReceiveAdapter

    private lateinit var socketDisconnect: Button

    private val fileNameMap by lazy {
        URLConnection.getFileNameMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler(Looper.getMainLooper())
        sendReceiveAdapter = SendReceiveAdapter(this, handler)

    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferRecycler = view.findViewById(R.id.fragment_transfer_recycler)

        transferRecycler.layoutManager = NonPredictiveLinearLayoutManager(requireContext())

        transferRecycler.adapter = sendReceiveAdapter

        transferRecycler.post {

            val intent = Intent(context, SocketCommunicationService::class.java)

            requireContext().bindService(
                intent,
                serviceConnectionListener,
                Context.BIND_AUTO_CREATE
            )


        }

        socketDisconnect = view.findViewById<Button?>(R.id.fragment_transfer_disconnect).also {

            it.setOnClickListener {
                socketCommunicationService.disconnect()
            }
        }

    }

    override fun onDestroyView() {

        socketCommunicationService.disconnect()
        requireContext().unbindService(serviceConnectionListener)

        super.onDestroyView()
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        return arrayListOf()
    }

    override fun getTAG(): String? {

        return TAG
    }

    override fun onSocketConnection() {

        val transferType = arguments?.getInt(TRANSFER_TYPE)!!

        handler.post {

            if (transferType == SEND) {

                socketCommunicationService.send(arguments?.getStringArrayList(FILE_PATHS)!!)
            } else {

                socketCommunicationService.receive()
            }
        }
    }

    override fun onSocketClose(ioError: Boolean) {

        socketCommunicationService.removeTransferEventListener()
        socketCommunicationService.removeConnectionListener()

        //Toast.makeText(requireContext(), "manual disconnect,finish", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(): Boolean {

        return true
    }

    @Synchronized
    override fun loadImage(imageView: ImageView, position: Int) {

        Executors.newSingleThreadExecutor().execute {

            val fileObject = sendReceiveAdapter.getItems()[position]

            val mimeType = fileNameMap.getContentTypeFor(fileObject.path)

            if (mimeType != null) {

                val metadataRetriever = MediaMetadataRetriever()

                when {

                    mimeType.startsWith("image/", true) -> {

                        val bitmap = BitmapFactory.decodeFile(fileObject.path)

                        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 240, 320, false)

                        imageView.apply {

                            post {
                                setImageBitmap(scaledBitmap)
                            }
                        }
                    }

                    mimeType.startsWith("audio/", true) -> {

                        metadataRetriever.setDataSource(fileObject.path)

                        val byteArray = metadataRetriever.embeddedPicture

                        if (byteArray != null) {

                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                            imageView.apply {

                                post {
                                    setImageBitmap(bitmap)
                                }
                            }

                        } else {

                            val d = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.ic_baseline_audiotrack_24
                            )

                            imageView.apply {

                                post {
                                    setImageDrawable(d)
                                }
                            }
                        }

                        metadataRetriever.close()
                    }

                    mimeType.startsWith("video/", true) -> {

                        metadataRetriever.setDataSource(fileObject.path)

                        val byteArray = metadataRetriever.embeddedPicture

                        if (byteArray != null) {

                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                            imageView.apply {

                                post {
                                    setImageBitmap(
                                        Bitmap.createScaledBitmap(
                                            bitmap,
                                            240,
                                            320,
                                            false
                                        )
                                    )
                                }
                            }

                        } else {

                            val pi = metadataRetriever.getFrameAtTime(0L)

                            if (pi != null) {

                                imageView.apply {

                                    post {
                                        setImageBitmap(
                                            Bitmap.createScaledBitmap(
                                                pi,
                                                240,
                                                320,
                                                false
                                            )
                                        )
                                    }
                                }

                            } else {

                                val d = ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.ic_baseline_insert_drive_file_24
                                )

                                imageView.apply {

                                    post {
                                        setImageDrawable(d)
                                    }
                                }
                            }
                        }

                    }

                    mimeType.equals("application/vnd.android.package-archive") || fileObject.name.endsWith(".apk", false) -> {

                        imageView.apply {

                            post {

                                var apkIcon:Drawable?

                                    try {

                                        val apkArchiveInfo = requireContext().packageManager.getPackageArchiveInfo(fileObject.path,0)
                                        //apkArchiveInfo?.applicationInfo?.sourceDir = contentData.path
                                        //apkArchiveInfo?.applicationInfo?.publicSourceDir = contentData.path

                                        apkIcon = apkArchiveInfo?.applicationInfo?.loadIcon(requireContext().packageManager)
                                    }catch (e: PackageManager.NameNotFoundException){

                                        apkIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_launcher_anyx_foreground)
                                    }
                                setImageDrawable(apkIcon)
                            }
                        }
                    }

                    else -> {

                        val d = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_insert_drive_file_24
                        )

                        imageView.apply {

                            post {
                                setImageDrawable(d)
                            }
                        }
                    }
                }

                metadataRetriever.close()
            }
        }
    }

    //user provided cancellation index
    override fun cancelAt(position: Int) {
        socketCommunicationService.cancelSendAt(position)
    }

    override fun onTransferSessionStart() {
        handler.post {
            socketDisconnect.isEnabled = false
        }
    }

    @Synchronized
    override fun onStartTransfer(file: File, maxLength: Long) {
        sendReceiveAdapter.add(file, SendReceiveAdapter.SEND, maxLength)
    }

    @Synchronized
    override fun onBytesTransferred(file: File, bytesTransferred: Int) {
        sendReceiveAdapter.update(file.name, bytesTransferred.toLong())
    }

    @Synchronized
    override fun onFinishTransfer(file: File) {
        sendReceiveAdapter.finish(file.name)
    }

    //skip for now TODO
    @Synchronized
    override fun onCancel(file: File) {
        sendReceiveAdapter.cancel(file.name)
    }

    @Synchronized
    override fun onStartReceive(file: File, maxLength: Long) {

        sendReceiveAdapter.add(file, SendReceiveAdapter.RECEIVE, maxLength)
    }

    @Synchronized
    override fun onBytesReceived(file: File, bytesReceived: Int) {
        sendReceiveAdapter.update(file.name, bytesReceived.toLong())
    }

    @Synchronized
    override fun onFinishReceive(file: File) {
        sendReceiveAdapter.finish(file.name)
    }

    @Synchronized
    override fun onSocketIOError() {

        socketCommunicationService.removeConnectionListener()
        socketCommunicationService.removeTransferEventListener()

        sendReceiveAdapter.transferError()
    }

    @Synchronized
    override fun onTransferSessionEnd() {

        socketCommunicationService.removeConnectionListener()
        socketCommunicationService.removeTransferEventListener()

        handler.post {
            socketDisconnect.isEnabled = true
        }
    }

    companion object {

        @JvmField
        val TAG: String = TransferFragment::class.simpleName!!

        const val FILE_PATHS = "file_paths"
        const val P2P_INFO = "p2p_info"
        const val TRANSFER_TYPE = "transfer_type"

        const val SEND = 1
        const val RECEIVE = 2
    }

    inner class NonPredictiveLinearLayoutManager(val context: Context) :
        LinearLayoutManager(context) {

        override fun supportsPredictiveItemAnimations(): Boolean {
            return false
        }

    }
}