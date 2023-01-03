package com.a.anyx.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.content.ContentStore
import com.a.anyx.fragment.adapter.ContentDataAdapter
import kotlinx.coroutines.launch

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


class VideoFragment : BaseFragment() {

    private lateinit var videoViewModel: VideoViewModel
    private lateinit var gridContentDataAdapter: ContentDataAdapter
    private lateinit var videoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(VideoViewModel::class.java)
        gridContentDataAdapter = ContentDataAdapter(requireContext(), arrayListOf(),ContentDataAdapter.GRID_VIEW_TYPE)

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

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable("selection",gridContentDataAdapter.getAdapterSelection())

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

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