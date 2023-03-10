package com.a.anyx.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.commit
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.dialog.WhatFileFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import com.a.anyx.viewmodel.ContentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class SongFragment : BaseFragment() {


    private lateinit var songViewModel: ContentViewModel

    private lateinit var linearContentDataAdapter: ContentDataAdapter
    private lateinit var songRecyclerView: RecyclerView

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

        songViewModel.sort(sortType,desc)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        songViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(ContentViewModel::class.java)
        linearContentDataAdapter  = ContentDataAdapter(
            this, arrayListOf(),
            ContentDataAdapter.LINEAR_VIEW_TYPE
        )

        if (savedInstanceState == null){

            songViewModel.loadData(SelectorFragment.SONGS_DATA)
            linearContentDataAdapter.setAdapterSelection(arrayListOf())
        }else{

            val selection = savedInstanceState.getStringArrayList("selection") as ArrayList<Long>
            linearContentDataAdapter.setAdapterSelection(selection)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        val selectedContents = ArrayList<String>().apply {

            songViewModel.data.value?.also {

                for(d in it){

                    if (linearContentDataAdapter.getAdapterSelection().contains(d.hashCode().toLong()))
                        add(d.path!!)
                }
            }
        }
        return selectedContents
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songRecyclerView = view.findViewById<RecyclerView?>(R.id.fragment_song_recycler).apply {

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = linearContentDataAdapter
        }

        songViewModel.data.observe(viewLifecycleOwner, Observer {

            linearContentDataAdapter.setData(it)
        })

        linearContentDataAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {

                if (view.id == R.id.base){

                    WhatFileFragment().also {

                        it.arguments = Bundle().apply {

                            putParcelable("content",songViewModel.data.value!![position])
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

        outState.putSerializable("selection",linearContentDataAdapter.getAdapterSelection())

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

                    val b = songViewModel.data.value?.get(position)?.uri?.let {
                        requireContext().contentResolver.loadThumbnail(
                            it, Size(240,320),null)
                    }

                    imageView.post {
                        imageView.setImageBitmap(b)
                    }
                }catch (e: Exception){

                    imageView.post {

                        imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_audiotrack_24))
                    }
                }
            }
        }
    }

    companion object{

        @JvmField val TAG:String = SongFragment::class.simpleName!!

        @JvmStatic val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = SongFragment()
    }
}