package com.a.anyx.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.a.anyx.content.ContentData
import com.a.anyx.content.ContentStore
import kotlin.collections.ArrayList


abstract class BaseFragment : Fragment() {

    abstract fun onBackPressed():Boolean

    abstract fun loadImage(imageView:ImageView,position:Int)

    abstract fun getSelectedFilePathItems():ArrayList<String>?

    abstract fun getTAG():String?
}