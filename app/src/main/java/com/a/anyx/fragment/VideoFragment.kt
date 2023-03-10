package com.a.anyx.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.dialog.WhatFileFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import com.a.anyx.viewmodel.ContentViewModel
import kotlinx.coroutines.internal.SynchronizedObject
import java.lang.Exception
import java.util.concurrent.Executors


class VideoFragment : BaseFragment() {

    private lateinit var videoViewModel: ContentViewModel
    private lateinit var gridContentDataAdapter: ContentDataAdapter
    private lateinit var videoRecyclerView: RecyclerView

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

        videoViewModel.sort(sortType,desc)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(ContentViewModel::class.java)
        gridContentDataAdapter = ContentDataAdapter(
            this, arrayListOf(),
            ContentDataAdapter.GRID_VIEW_TYPE
        )

        if (savedInstanceState == null){

            videoViewModel.loadData(SelectorFragment.VIDEOS_DATA)
            gridContentDataAdapter.setAdapterSelection(arrayListOf())
        }else{

            val selection = savedInstanceState.getStringArrayList("selection") as ArrayList<Long>
            gridContentDataAdapter.setAdapterSelection(selection)
        }


    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        val selectedContents = ArrayList<String>().apply {

            videoViewModel.data.value?.also {

                for (d in it){

                    if (gridContentDataAdapter.getAdapterSelection().contains(d.hashCode().toLong()))
                        add(d.path!!)
                }
            }
        }

        return selectedContents
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoRecyclerView = view.findViewById<RecyclerView?>(R.id.fragment_video_recycler).apply {

            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = gridContentDataAdapter
        }

        videoViewModel.data.observe(viewLifecycleOwner, Observer {

           gridContentDataAdapter.setData(it)
        })

        gridContentDataAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {

                if (view.id == R.id.base){

                    WhatFileFragment().also {

                        it.arguments = Bundle().apply {

                            putParcelable("content",videoViewModel.data.value!![position])
                        }

                        requireActivity().supportFragmentManager.commit {
                            setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                            addToBackStack(null)
                            replace(R.id.activity_send_navigator,it,WhatFileFragment.TAG)
                        }


                    }



                }

                if (view.id == R.id.checker){

                    requireActivity().supportFragmentManager.findFragmentById(R.id.activity_send_navigator)?.also { it->

                        if (it is SelectorFragment) it.updateSelectionCount()
                    }
                }

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable("selection",gridContentDataAdapter.getAdapterSelection())

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun loadImage(imageView: ImageView, position: Int) {

        handler.post {
            Executors.newSingleThreadExecutor().execute {

                try {

                    val b = videoViewModel.data.value?.get(position)?.uri?.let {
                        requireContext().contentResolver.loadThumbnail(
                            it, Size(240,320),null)
                    }

                    imageView.post {

                        imageView.setImageBitmap(b)
                    }
                }catch (e: Exception){

                    imageView.post {

                        imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_movie_24))
                    }
                }
            }
        }
    }


    companion object {

        @JvmField
        val TAG: String = VideoFragment::class.simpleName!!

        @JvmStatic
        val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = VideoFragment()
    }
}