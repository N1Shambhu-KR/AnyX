package com.a.anyx.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.content.ContentStore
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class SongViewModel(private val app: Application): AndroidViewModel(app){

    private val songData = MutableLiveData<ArrayList<ContentData>>()

    val data: LiveData<ArrayList<ContentData>>
        get() = songData

    fun loadSongData(){

        val store = ContentStore(app)

        viewModelScope.launch {

            store.collectSongs()
             songData.value = store.getSongData()
         }

    }

}


class SongFragment : BaseFragment() {

    private lateinit var songViewModel: SongViewModel

    private lateinit var linearContentDataAdapter: ContentDataAdapter
    private lateinit var songRecyclerView: RecyclerView

    override fun getTAG(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        songViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(SongViewModel::class.java)
        linearContentDataAdapter  = ContentDataAdapter(requireContext(), arrayListOf(),ContentDataAdapter.LINEAR_VIEW_TYPE)

        if (savedInstanceState == null){

            songViewModel.loadSongData()
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

            for(d in songViewModel.data.value!!){

                if (linearContentDataAdapter.getAdapterSelection().contains(d.hashCode().toLong()))
                    add(d.path!!)
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

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable("selection",linearContentDataAdapter.getAdapterSelection())

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

    }

    companion object{

        @JvmField val TAG:String = SongFragment::class.simpleName!!

        @JvmStatic val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = SongFragment()
    }
}