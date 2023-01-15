package com.a.anyx.fragment

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.content.ContentStore
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.Executors


class VideoFragment : BaseFragment() {

    class VideoViewModel(private val app: Application): AndroidViewModel(app){

        private val videoData = MutableLiveData<ArrayList<ContentData>>()

        val data: LiveData<ArrayList<ContentData>>
            get() = videoData

        fun loadVideoData(){

            val store = ContentStore(app)

            viewModelScope.launch {

                store.collectVideos()
                videoData.value = store.getVideoData()
            }

        }

    }


    private lateinit var videoViewModel: VideoViewModel
    private lateinit var gridContentDataAdapter: ContentDataAdapter
    private lateinit var videoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(VideoViewModel::class.java)
        gridContentDataAdapter = ContentDataAdapter(
            this, arrayListOf(),
            ContentDataAdapter.GRID_VIEW_TYPE
        )

        if (savedInstanceState == null){

            videoViewModel.loadVideoData()
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

            for (d in videoViewModel.data.value!!){

                if (gridContentDataAdapter.getAdapterSelection().contains(d.hashCode().toLong()))
                    add(d.path!!)
            }
        }

        return selectedContents
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoRecyclerView = view.findViewById<RecyclerView?>(R.id.fragment_video_recycler).apply {

            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = gridContentDataAdapter
        }

        videoViewModel.data.observe(viewLifecycleOwner, Observer {

           gridContentDataAdapter.setData(it)
        })

        gridContentDataAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {


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

    companion object {

        @JvmField
        val TAG: String = VideoFragment::class.simpleName!!

        @JvmStatic
        val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = VideoFragment()
    }
}