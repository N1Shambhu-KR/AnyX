package com.a.anyx.fragment

import android.app.Application
import android.content.Context
import android.os.*
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.content.ContentStore
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import kotlinx.coroutines.launch
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.lang.Exception
import java.util.concurrent.Executors


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

    override fun onBackPressed(): Boolean {
        return true
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
        gridContentDataAdapter = ContentDataAdapter(this, arrayListOf(), ContentDataAdapter.GRID_VIEW_TYPE)

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

            setItemViewCacheSize(8)
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),4)

                adapter = gridContentDataAdapter

        }

        imageViewModel.data.observe(viewLifecycleOwner, Observer {

            gridContentDataAdapter.setData(it)
        })

        gridContentDataAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {

            }
        })

        FastScrollerBuilder(imageRecyclerView).build()

    }

    override fun onSaveInstanceState(outState: Bundle) {

        Log.d(ImageFragment.TAG,"onSavedInstanceState Called")
        outState.putSerializable("selection",gridContentDataAdapter.getAdapterSelection())

        super.onSaveInstanceState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun loadImage(imageView: ImageView, position: Int) {

        Executors.newSingleThreadExecutor().execute {

            try {

                val b = imageViewModel.data.value?.get(position)?.uri?.let {
                    requireContext().contentResolver.loadThumbnail(
                        it,Size(240,320),null)
                }

                imageView.post {

                    imageView.setImageBitmap(b)
                }
            }catch (e:Exception){

                imageView.post {

                    imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_image_24))
                }
            }
        }
    }

    companion object{

        @JvmField val TAG:String = ImageFragment::class.simpleName!!

        @JvmStatic val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = ImageFragment()
    }

}