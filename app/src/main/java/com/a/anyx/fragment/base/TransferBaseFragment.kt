package com.a.anyx.fragment.base

import android.widget.ImageView
import com.a.anyx.fragment.SelectorFragment

abstract class TransferBaseFragment :BaseFragment() {

    abstract fun cancelAt(position:Int)
}