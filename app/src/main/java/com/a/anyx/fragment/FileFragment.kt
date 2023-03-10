package com.a.anyx.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.dialog.WhatFileFragment
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import java.io.File
import java.net.URLConnection
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileFragment : BaseFragment() {

    private lateinit var fileStack:Stack<String>

    private lateinit var fileStackLookup: HashMap<String,ArrayList<String>>

    private var currentDirectoryFileList:ArrayList<ContentData> = ArrayList()

    private lateinit var filesRecycler:RecyclerView
    private lateinit var filesAdapter: ContentDataAdapter

    private lateinit var absolutePath:String

    private lateinit var absolutePathIndicator: TextView

    private val fileNameMap by lazy {
        URLConnection.getFileNameMap()
    }

    private val handler by lazy {

        Handler(Looper.getMainLooper())
    }

    override fun sortList(sortType: SelectorFragment.SortType, desc: Boolean) {

        Executors.newSingleThreadExecutor().execute {

            val sorted = currentDirectoryFileList.sortedWith { o1, o2 ->

                when (sortType) {

                    SelectorFragment.SortType.SORT_TYPE_NAME -> {

                        if (!desc) {
                            o1?.name!!.compareTo(o2?.name!!)
                        } else {
                            o2?.name!!.compareTo(o1?.name!!)
                        }
                    }

                    SelectorFragment.SortType.SORT_TYPE_DATE -> {

                        if (desc) {
                            o1?.date!!.compareTo(o2?.date!!)
                        } else {
                            o2?.date!!.compareTo(o1?.date!!)
                        }
                    }

                    SelectorFragment.SortType.SORT_TYPE_LENGTH -> {

                        if (desc) {
                            o1?.length!!.compareTo(o2?.length!!)
                        } else {
                            o2?.length!!.compareTo(o1?.length!!)
                        }
                    }

                    else -> {
                        -1
                    }
                }
            }

            currentDirectoryFileList.clear()
            currentDirectoryFileList.addAll(sorted)

            filesRecycler.post {

                filesAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun updateList(path:String){

        currentDirectoryFileList.clear()

        val file = File(path)

        if (file.isDirectory){

            if (file.listFiles() != null){

                for (f in file.listFiles()!!){

                    var mimeType = "N/A"

                    if (!f.isDirectory)
                    {
                        fileNameMap.getContentTypeFor(f.name)?.also {
                            mimeType = it
                        }
                    }else{
                        mimeType = "folder"
                    }

                    currentDirectoryFileList.add(ContentData(
                        f.name,
                        Uri.fromFile(f),
                        f.absolutePath,
                        f.length(),
                        f.lastModified(),
                        mimeType
                    ))
                }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (savedInstanceState == null){

            fileStack = Stack()
            fileStack.push(Environment.getExternalStorageDirectory().absolutePath)

            absolutePath = Environment.getExternalStorageDirectory().absolutePath
            fileStackLookup = HashMap()

            updateList(fileStack.peek())

            filesAdapter  = ContentDataAdapter(this, currentDirectoryFileList, ContentDataAdapter.LINEAR_VIEW_TYPE)
            filesAdapter.setAdapterSelection(arrayListOf())

        }else{

            fileStack = savedInstanceState.getSerializable(FILE_STACK) as Stack<String>
            absolutePath = savedInstanceState.getString(ABSOLUTE_PATH,"")
            fileStackLookup = savedInstanceState.getSerializable(FILE_STACK_LOOKUP) as HashMap<String, ArrayList<String>>

            updateList(fileStack.peek())

            val selection = savedInstanceState.getStringArrayList("selection") as ArrayList<Long>

            filesAdapter = ContentDataAdapter(this, currentDirectoryFileList, ContentDataAdapter.LINEAR_VIEW_TYPE)
            filesAdapter.setAdapterSelection(selection)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        absolutePathIndicator = view.findViewById<TextView>(R.id.fragment_file_absolute_path_indicator).apply {

            text = absolutePath
        }

        filesRecycler = view.findViewById(R.id.fragment_file_recycler)
        filesRecycler.adapter = filesAdapter
        filesRecycler.layoutManager = LinearLayoutManager(requireContext())

        filesAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {


                if (view.id == R.id.base){

                    val newFileStackPath = "${fileStack.peek()}${File.separator}${currentDirectoryFileList.get(position).name}"

                    Toast.makeText(requireContext(),absolutePath,Toast.LENGTH_SHORT).show()

                    File(newFileStackPath).also {

                        if (it.exists()){

                            if (it.isDirectory){

                                updateList(newFileStackPath)
                                filesAdapter.notifyDataSetChanged()

                                absolutePathIndicator.apply {

                                    absolutePath = "$newFileStackPath"
                                    text = absolutePath
                                }

                                fileStack.push(newFileStackPath)
                            }else{


                                WhatFileFragment().also {

                                    it.arguments = Bundle().apply {

                                        putParcelable("content",currentDirectoryFileList.get(position))
                                    }


                                        requireActivity().supportFragmentManager.commit {
                                            setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                                            addToBackStack(null)
                                            replace(R.id.activity_send_navigator,it,WhatFileFragment.TAG)
                                        }

                                }



                            }
                        }
                    }

                }else if(view.id == R.id.checker){

                    if (filesAdapter.getAdapterSelection().contains(currentDirectoryFileList[position].hashCode().toLong())){
                        //Toast.makeText(requireContext(),"${currentDirectoryFileList[position].path}",Toast.LENGTH_SHORT).show()

                            if (fileStackLookup.containsKey(fileStack.peek())){

                            fileStackLookup.get(fileStack.peek())?.also {
                                it.add(currentDirectoryFileList[position].path!!)
                            }
                        }else{

                            fileStackLookup.let {
                                it.put(fileStack.peek(), arrayListOf(currentDirectoryFileList[position].path!!))
                            }
                        }
                    }else{
                        fileStackLookup.let {

                            it.get(fileStack.peek())?.also { arrayList->

                                arrayList.remove(currentDirectoryFileList[position].path)
                                if (arrayList.isEmpty())it.remove(fileStack.peek())
                            }
                        }
                        //Toast.makeText(requireContext(),"cntaining",Toast.LENGTH_SHORT).show()

                    }

                    requireActivity().supportFragmentManager.findFragmentById(R.id.activity_send_navigator)?.also { it->

                        if (it is SelectorFragment) it.updateSelectionCount()
                    }
                }

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable("selection",filesAdapter.getAdapterSelection())
        outState.putSerializable(FILE_STACK,fileStack)
        outState.putString(ABSOLUTE_PATH,absolutePath)
        outState.putSerializable(FILE_STACK_LOOKUP,fileStackLookup)
        super.onSaveInstanceState(outState)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {

        val selectedContents = ArrayList<String>().apply {

            for (d in fileStackLookup.keys){

                fileStackLookup.get(d)?.also {

                    addAll(it)
                }
            }
        }

         return selectedContents
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onBackPressed(): Boolean {
        if (fileStack.size>1){

            fileStack.pop()

            val path = fileStack.peek()

            updateList(path)

            absolutePathIndicator.text = path

            filesAdapter.notifyDataSetChanged()

            return false
        }else{

            return true
        }
    }

    override fun loadImage(imageView: ImageView, position: Int) {

        val currentOperation = currentDirectoryFileList[position]

        when{

            currentOperation.mimeType!!.equals("folder")->{

              imageView.setImageResource(R.drawable.ic_baseline_folder_24)
            }
        }


    }


    companion object {

        @JvmField val TAG:String = FileFragment::class.simpleName!!

        const val FILE_STACK = "file_stack"
        const val ABSOLUTE_PATH = "absolute_path"
        const val FILE_STACK_LOOKUP = "file_stack_lookup"

        @JvmStatic
        fun newInstance() = FileFragment()

    }

}