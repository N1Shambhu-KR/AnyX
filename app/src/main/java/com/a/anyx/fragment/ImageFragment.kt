package com.a.anyx.fragment

import android.app.Application
import android.content.Context
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.content.ContentStore
import com.a.anyx.interfaces.IOnFragment
import kotlinx.coroutines.launch
import me.zhanghai.android.fastscroll.FastScrollerBuilder


class ImageFragment : BaseFragment() {

    class ImageViewModel(private val app:Application):AndroidViewModel(app){

        private val imageData = MutableLiveData<ArrayList<ContentData>>()

        val data:LiveData<ArrayList<ContentData>>
            get() = imageData

        fun loadImageData(){

            val store = ContentStore(app)

            viewModelScope.launch {

                store.collectImages()
                imageData.value = store.getImageData()
            }

        }

    }


    private lateinit var imageViewModel: ImageViewModel

    private lateinit var gridContentDataAdapter: ContentDataAdapter
    private lateinit var imageRecyclerView: RecyclerView

    private lateinit var selection: ArrayList<Long>

    private lateinit var iOnFragment:IOnFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)

        iOnFragment = context as IOnFragment
        iOnFragment.onFragment(this)

    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        val selectedContents = ArrayList<String>().apply {

            for (d in imageViewModel.data.value!!){

                if (gridContentDataAdapter.getAdapterSelection().contains(d.hashCode().toLong()))
                    add(d.path!!)
            }
        }


        return selectedContents
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(ImageViewModel::class.java)
        gridContentDataAdapter = ContentDataAdapter(requireContext(), arrayListOf(),ContentDataAdapter.GRID_VIEW_TYPE)

        if (savedInstanceState == null){

            imageViewModel.loadImageData()
            gridContentDataAdapter.setAdapterSelection(arrayListOf())
        }else{

            val selection = savedInstanceState.getStringArrayList("selection") as ArrayList<Long>
            gridContentDataAdapter.setAdapterSelection(selection)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG,"onCreateView")
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageRecyclerView = view.findViewById<RecyclerView?>(R.id.fragment_image_recycler).apply {

            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),4)
            adapter = gridContentDataAdapter
        }

        imageViewModel.data.observe(viewLifecycleOwner, Observer {

            gridContentDataAdapter.setData(it)
        })

        FastScrollerBuilder(imageRecyclerView).build()

    }

    override fun onSaveInstanceState(outState: Bundle) {

        Log.d(ImageFragment.TAG,"onSavedInstanceState Called")
        outState.putSerializable("selection",gridContentDataAdapter.getAdapterSelection())

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

    }


    companion object{

        @JvmField val TAG:String = ImageFragment::class.simpleName!!

        @JvmStatic val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = ImageFragment()
    }

}