package com.a.anyx.fragment

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.a.anyx.R
import com.a.anyx.content.ContentData
import kotlin.collections.ArrayList

class FileFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return arrayListOf()
    }
    override fun onBackPressed() {

    }

    override fun getTAG(): String? {
        return TAG
    }

    override fun onPermissionChanged() {

    }

    companion object {

        @JvmField val TAG:String = FileFragment::class.simpleName!!

        @JvmStatic val DATA = ContentData::class.simpleName

        @JvmStatic
        fun newInstance() = FileFragment()

    }

}