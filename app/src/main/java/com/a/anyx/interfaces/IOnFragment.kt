package com.a.anyx.interfaces

import androidx.fragment.app.Fragment
import com.a.anyx.fragment.BaseFragment

interface IOnFragment {

    fun onFragment(fragment: Fragment)

    fun onFragmentVisibility(fragment:Fragment)

}