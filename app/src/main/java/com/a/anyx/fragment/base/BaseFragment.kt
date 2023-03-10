package com.a.anyx.fragment.base

import android.widget.ImageView
import androidx.fragment.app.Fragment
import kotlin.collections.ArrayList
import com.a.anyx.fragment.SelectorFragment.SortType


abstract class BaseFragment : Fragment() {

    abstract fun onBackPressed():Boolean

    abstract fun sortList(sortType: SortType,desc:Boolean)

    abstract fun loadImage(imageView:ImageView,position:Int)

    abstract fun getSelectedFilePathItems():ArrayList<String>?

    abstract fun getTAG():String?
}