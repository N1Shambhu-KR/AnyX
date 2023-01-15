package com.a.anyx.fragment

import android.net.Uri
import android.os.*
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.a.anyx.R
import com.a.anyx.content.ContentData
import com.a.anyx.fragment.adapter.ContentDataAdapter
import com.a.anyx.interfaces.OnRecyclerViewItemClick
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class FileFragment : BaseFragment() {

    private lateinit var fileStack:Stack<String>

    private var fileList:ArrayList<ContentData> = ArrayList()

    private lateinit var filesRecycler:RecyclerView
    private lateinit var filesAdapter: ContentDataAdapter

    private fun updateList(path:String){

        fileList.clear()

        val file = File(path)

        if (file.isDirectory){

            if (file.listFiles() != null){

                for (f in file.listFiles()!!){

                    fileList.add(ContentData(f.name, Uri.fromFile(f),f.absolutePath,f.length(),f.lastModified()))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null){

            fileStack = Stack()
            fileStack.push(Environment.getExternalStorageDirectory().absolutePath)

            updateList(fileStack.peek())

            filesAdapter  = ContentDataAdapter(this, fileList, ContentDataAdapter.LINEAR_VIEW_TYPE)
            filesAdapter.setAdapterSelection(arrayListOf())
        }else{

            fileStack = savedInstanceState.getSerializable(FILE_STACK) as Stack<String>

            updateList(fileStack.peek())

            val selection = savedInstanceState.getStringArrayList("selection") as ArrayList<Long>

            filesAdapter = ContentDataAdapter(this, fileList, ContentDataAdapter.LINEAR_VIEW_TYPE)
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

        filesRecycler = view.findViewById(R.id.fragment_file_recycler)
        filesRecycler.adapter = filesAdapter
        filesRecycler.layoutManager = LinearLayoutManager(requireContext())

        filesAdapter.setRecyclerViewItemClickListener(object :OnRecyclerViewItemClick{

            override fun onItemClick(position: Int, view: View) {

                if (view.id == R.id.base){

                    val absolutePath = "${fileStack.peek()}${File.separator}${fileList.get(position).name}"

                    Toast.makeText(requireContext(),absolutePath,Toast.LENGTH_SHORT).show()

                    if (File(absolutePath).exists()){

                        updateList(absolutePath)
                        filesAdapter.notifyDataSetChanged()

                        fileStack.push(absolutePath)
                    }

                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putSerializable("selection",filesAdapter.getAdapterSelection())
        outState.putSerializable(FILE_STACK,fileStack)
        super.onSaveInstanceState(outState)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return arrayListOf()
    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onBackPressed(): Boolean {
        if (fileStack.size>1){

            fileStack.pop()

            val absolutePath = fileStack.peek()

            updateList(absolutePath)

            filesAdapter.notifyDataSetChanged()

            return false
        }else{

            return true
        }
    }

    override fun loadImage(imageView: ImageView, position: Int) {

        Executors.newSingleThreadExecutor().execute {

            val f = File(fileList[position].path!!)

            if (f.isDirectory){

                    val d = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_folder_24)

                    val values = TypedValue()
                    requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary,values,true)

                    d?.setTint(values.data)

                imageView.post {
                    imageView.setImageDrawable(d!!)
                }

            }else{

                    val d = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_note_24)

                    val values = TypedValue()
                    requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary,values,true)

                    d?.setTint(values.data)

                    imageView.post {
                        imageView.setImageDrawable(d!!)
                    }

            }
        }
    }

    companion object {

        @JvmField val TAG:String = FileFragment::class.simpleName!!

        const val FILE_STACK = "file_stack"

        @JvmStatic
        fun newInstance() = FileFragment()

    }


}