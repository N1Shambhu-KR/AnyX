package com.a.anyx.fragment.dialog

import android.annotation.SuppressLint
import  android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView

import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.SelectorFragment
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.util.DataUtils
import com.a.anyx.util.TimeUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class WhatFileFragment: BaseFragment(){

    private lateinit var mediaHolder : ConstraintLayout

    private lateinit var mediaActionPlay:Button
    private lateinit var mediaActionPause:Button

    private lateinit var mediaSlider: Slider

    private lateinit var durationZero:TextView
    private lateinit var durationMax:TextView

    private lateinit var content:ContentData

    private lateinit var materialDialog : View

    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var scrollDownFAB : FloatingActionButton

    private lateinit var _videoView:VideoView
    private lateinit var _mediaPlayer:MediaPlayer

    private lateinit var progressUpdateRunnable:Runnable
    private val handler:Handler by lazy {
        Handler(Looper.getMainLooper())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        content = arguments?.getParcelable<ContentData>("content") as ContentData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dialog_what_file,container,false)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      mediaHolder = view.findViewById(R.id.dialog_what_file_content_holder)
        val mediaHolderParams = (mediaHolder).layoutParams as ConstraintLayout.LayoutParams

        durationZero = view.findViewById(R.id.dialog_what_file_duration_zero)
        durationMax = view.findViewById(R.id.dialog_what_file_duration_max)

        mediaActionPlay = view.findViewById(R.id.dialog_what_file_play_media)
        mediaActionPause = view.findViewById(R.id.bottom_dialog_what_file_pause_media)

        mediaSlider = view.findViewById(R.id.bottom_dialog_what_file_media_slider)

        scrollDownFAB = view.findViewById<FloatingActionButton>(R.id.dialog_what_file_scroll_down_fab).apply {

            setOnClickListener {
                nestedScrollView.fullScroll(View.FOCUS_DOWN)
            }
        }


        nestedScrollView = view.findViewById<NestedScrollView>(R.id.dialog_what_file_nested_scroll).apply {

            post {
                canScrollVertically(View.FOCUS_DOWN).let {

                    scrollDownFAB.visibility = if (it) View.VISIBLE else View.GONE

                }
            }

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                if (v.canScrollVertically(View.FOCUS_DOWN) && scrollDownFAB.visibility != View.VISIBLE){
                    //Toast.makeText(requireContext(),"fab should visible",Toast.LENGTH_SHORT).show()
                    scrollDownFAB.visibility = View.VISIBLE
                }else if(!v.canScrollVertically(View.FOCUS_DOWN) && scrollDownFAB.visibility == View.VISIBLE){
                    //Toast.makeText(requireContext(),"fab shouldn't visible",Toast.LENGTH_SHORT).show()
                    scrollDownFAB.visibility = View.GONE
                }
            }
        }

        view.findViewById<Button>(R.id.bottom_dialog_what_file_close).apply {

            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        view.findViewById<TextView>(R.id.dialog_what_filename).text = content.name
        view.findViewById<TextView>(R.id.dialog_what_file_type).text = content.mimeType
        view.findViewById<TextView>(R.id.dialog_what_file_size).text = DataUtils.bytesToReadableFormat(content.length!!)

        addViewToMediaHolder(content)
    }

    override fun onBackPressed(): Boolean {

        return true
    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

    }

    override fun loadImage(imageView: ImageView, position: Int) {

    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return arrayListOf()
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onDestroy() {

        when{

            content.mimeType?.startsWith("video/",true)!! ->{

                _videoView.stopPlayback()
                _videoView.suspend()

            }

            content.mimeType?.startsWith("audio/",true)!!->{
                _mediaPlayer.stop()
                _mediaPlayer.reset()
                _mediaPlayer.release()

            }
        }

        super.onDestroy()
    }

     fun addViewToMediaHolder(contentData: ContentData){

         view?.findViewById<TextView>(R.id.dialog_what_filename)?.text = contentData.name
         view?.findViewById<TextView>(R.id.dialog_what_file_type)?.text = contentData.mimeType
         view?.findViewById<TextView>(R.id.dialog_what_file_size)?.text = DataUtils.bytesToReadableFormat(contentData.length!!)

         val mimeType = contentData.mimeType

        when{

            mimeType == null ->{

            }

            mimeType.equals("application/vnd.android.package-archive") || mimeType.endsWith(".apk",false)->{

                goneMediaControls()

                val imageContain = ImageView(requireContext())
                imageContain.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

                mediaHolder.removeAllViews()

                mediaHolder.addView(imageContain)

                imageContain.apply {

                    try {

                        val apkArchiveInfo = requireContext().packageManager.getPackageArchiveInfo(contentData.path!!,0)
                        //apkArchiveInfo?.applicationInfo?.sourceDir = contentData.path
                        //apkArchiveInfo?.applicationInfo?.publicSourceDir = contentData.path

                        setImageDrawable(apkArchiveInfo?.applicationInfo?.loadIcon(requireContext().packageManager))
                    }catch (e:PackageManager.NameNotFoundException){

                        setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_launcher_anyx_foreground))
                    }
                }

            }


            mimeType.startsWith("image/",true)->{

                setUpImageViewerInterface(contentData)
            }

            mimeType.startsWith("audio/",true)->{

                setUpAudioPlayerInterface(contentData)
            }

            mimeType.startsWith("video/",true)->{

               setUpVideoPlayerInterface(contentData)
            }

            else->{

                goneMediaControls()

                val imageContain = ImageView(requireContext())
                imageContain.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

                mediaHolder.removeAllViews()
                mediaHolder.addView(imageContain)

                imageContain.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_insert_drive_file_24))
            }
        }

    }

    private fun goneMediaControls(){

        mediaActionPause.visibility = View.GONE
        mediaActionPlay.visibility = View.GONE
        mediaSlider.visibility = View.GONE

        durationZero.visibility = View.GONE
        durationMax.visibility = View.GONE
    }

    private fun appearMediaControls(){

        mediaActionPause.visibility = View.VISIBLE
        mediaActionPlay.visibility = View.VISIBLE
        mediaSlider.visibility = View.VISIBLE

        durationZero.visibility = View.VISIBLE
        durationMax.visibility = View.VISIBLE
    }

    private fun setUpAudioPlayerInterface(contentData: ContentData){

        appearMediaControls()

        val imageContain = ImageView(requireContext())
        imageContain.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        mediaHolder.removeAllViews()
        mediaHolder.addView(imageContain)
        var bitmap:Bitmap

        imageContain.also {

            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(contentData.path)

            val byteArray = mediaMetadataRetriever.embeddedPicture

            durationZero.text = "00:00"
            durationMax.text = TimeUtils.milliSecondsToHHMMSS(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong())

            mediaSlider.valueTo = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toFloat()
            mediaSlider.setLabelFormatter {

                TimeUtils.milliSecondsToHHMMSS(it.toLong())

            }

            if (byteArray != null){

                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)

                it.setImageBitmap(bitmap)
            }else{

                it.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_audiotrack_24))
            }

            mediaMetadataRetriever.release()
            mediaMetadataRetriever.close()
        }

        val audioPlayer = MediaPlayer().also {

            it.setDataSource(contentData.path)

            it.setOnCompletionListener {

                //it.release()
                requireActivity().onBackPressed()
            }

            _mediaPlayer = it
        }

        audioPlayer.prepare()
        audioPlayer.setOnSeekCompleteListener {

            audioPlayer.start()
            mediaSlider.post(progressUpdateRunnable)
        }

        mediaSlider.valueTo = audioPlayer.duration.toFloat()
        mediaSlider.setLabelFormatter {

            TimeUtils.milliSecondsToHHMMSS(it.toLong())

        }

        progressUpdateRunnable = Runnable {

            try {

                mediaSlider.value = max(0,min(audioPlayer.currentPosition,audioPlayer.duration)).toFloat()
                view?.findViewById<TextView>(R.id.dialog_what_file_duration_zero)?.text = TimeUtils.milliSecondsToHHMMSS(audioPlayer.currentPosition.toLong())

                mediaSlider.postDelayed(progressUpdateRunnable,1000)

            }catch (i:IllegalStateException){

            }
        }

        audioPlayer.start()

        mediaSlider.post(progressUpdateRunnable)

        mediaSlider.addOnSliderTouchListener(object :Slider.OnSliderTouchListener{

            override fun onStartTrackingTouch(slider: Slider) {

                mediaSlider.removeCallbacks(progressUpdateRunnable)
            }

            override fun onStopTrackingTouch(slider: Slider) {

                audioPlayer.pause()
                audioPlayer.seekTo(slider.value.roundToInt())
            }
        })

        view?.findViewById<Button>(R.id.dialog_what_file_play_media)?.setOnClickListener {

            if (!audioPlayer.isPlaying) {
                audioPlayer.start()
            }
        }

        view?.findViewById<Button>(R.id.bottom_dialog_what_file_pause_media)?.setOnClickListener {

            if (audioPlayer.isPlaying) {
                audioPlayer.pause()
            }
        }
    }

    private fun setUpImageViewerInterface(contentData: ContentData){

        goneMediaControls()

        val imageContain = ImageView(requireContext())
        imageContain.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        mediaHolder.removeAllViews()
        imageContain.id = View.generateViewId()
        //imageContain.scaleType = ImageView.ScaleType.MATRIX
        mediaHolder.addView(imageContain)

        val defaultHolderConstraintSet = ConstraintSet()
        defaultHolderConstraintSet.clone(mediaHolder)
        defaultHolderConstraintSet.connect(imageContain.id,ConstraintSet.START,mediaHolder.id,ConstraintSet.START)
        defaultHolderConstraintSet.connect(imageContain.id,ConstraintSet.END,mediaHolder.id,ConstraintSet.END)
        defaultHolderConstraintSet.connect(imageContain.id,ConstraintSet.TOP,mediaHolder.id,ConstraintSet.TOP)
        defaultHolderConstraintSet.connect(imageContain.id,ConstraintSet.BOTTOM,mediaHolder.id,ConstraintSet.BOTTOM)
        defaultHolderConstraintSet.constrainWidth(imageContain.id,ConstraintLayout.LayoutParams.MATCH_PARENT)
        defaultHolderConstraintSet.constrainHeight(imageContain.id,ConstraintLayout.LayoutParams.MATCH_PARENT)
        defaultHolderConstraintSet.applyTo(mediaHolder)
        var bitmap:Bitmap

        Executors.newSingleThreadExecutor().execute {

            bitmap = BitmapFactory.decodeFile(contentData.path)

            imageContain.apply {

                post {
                    setImageBitmap(bitmap)

                }
            }
        }
    }

    private fun setUpVideoPlayerInterface(contentData: ContentData){

        appearMediaControls()
        var videoPlayer:MediaPlayer? = null

        val videoView = VideoView(requireContext())
        videoView.id = View.generateViewId()

        videoView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT).also {

            it.gravity = Gravity.CENTER
        }

        mediaHolder.removeAllViews()

        mediaHolder.apply {
            addView(videoView)
        }

        _videoView = videoView

        progressUpdateRunnable = Runnable {

            try {

                mediaSlider.value = max(0,min(videoPlayer?.currentPosition!!,videoPlayer?.duration!!)).toFloat()
                view?.findViewById<TextView>(R.id.dialog_what_file_duration_zero)?.text = TimeUtils.milliSecondsToHHMMSS(videoView.currentPosition.toLong())
                mediaSlider.postDelayed(progressUpdateRunnable,1000)

            }catch (i:IllegalStateException){

            }
        }

        val defaultHolderConstraintSet = ConstraintSet()
        defaultHolderConstraintSet.clone(mediaHolder)
        defaultHolderConstraintSet.connect(videoView.id,ConstraintSet.START,mediaHolder.id,ConstraintSet.START)
        defaultHolderConstraintSet.connect(videoView.id,ConstraintSet.END,mediaHolder.id,ConstraintSet.END)
        defaultHolderConstraintSet.connect(videoView.id,ConstraintSet.TOP,mediaHolder.id,ConstraintSet.TOP)
        defaultHolderConstraintSet.connect(videoView.id,ConstraintSet.BOTTOM,mediaHolder.id,ConstraintSet.BOTTOM)
        defaultHolderConstraintSet.constrainWidth(videoView.id,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        defaultHolderConstraintSet.constrainHeight(videoView.id,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        defaultHolderConstraintSet.applyTo(mediaHolder)

        videoView.setVideoPath(contentData.path)

        val metadataRetriever = MediaMetadataRetriever()

        try {

            metadataRetriever.setDataSource(contentData.path)
            view?.findViewById<TextView>(R.id.dialog_what_file_duration_zero)?.text = "00:00"
            view?.findViewById<TextView>(R.id.dialog_what_file_duration_max)?.text = TimeUtils.milliSecondsToHHMMSS(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong())

            mediaSlider.valueTo = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toFloat()
            mediaSlider.setLabelFormatter {

                TimeUtils.milliSecondsToHHMMSS(it.toLong())

            }

        }catch (i:IllegalArgumentException){

        }catch (e:IOException){

        }finally {
            metadataRetriever.release()
            metadataRetriever.close()
        }

        videoView.setOnPreparedListener {

            videoPlayer = it

            videoPlayer?.start()

            it.setOnSeekCompleteListener {

                videoPlayer?.start()
                mediaSlider.post(progressUpdateRunnable)
            }

            mediaSlider.post(progressUpdateRunnable)
        }

        mediaSlider.addOnSliderTouchListener(object :Slider.OnSliderTouchListener{

            override fun onStartTrackingTouch(slider: Slider) {

                mediaSlider.removeCallbacks(progressUpdateRunnable)
            }

            override fun onStopTrackingTouch(slider: Slider) {
                videoPlayer?.pause()
                videoPlayer?.seekTo(slider.value.roundToInt())

            }
        })

        videoView.setOnCompletionListener {

            requireActivity().onBackPressed()
        }

        view?.findViewById<Button>(R.id.dialog_what_file_play_media)?.setOnClickListener {

            if (!videoView.isPlaying ) videoView.start()

        }

        view?.findViewById<Button>(R.id.bottom_dialog_what_file_pause_media)?.setOnClickListener {

            if (videoView.isPlaying) videoView.pause()
        }

    }

    companion object{

        @JvmField val TAG = WhatFileFragment::class.simpleName
    }
}