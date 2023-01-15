package com.a.anyx.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.a.anyx.R
import com.a.anyx.fragment.*
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.util.StateResolver

class ReceiveActivity:AppCompatActivity(),IOnFragment{

    private lateinit var currentFragment:BaseFragment

    private lateinit var searchDeviceFragment: SearchDeviceFragment

    private lateinit var stateResolver:StateResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        stateResolver = StateResolver(this)

        if (savedInstanceState == null){

            searchDeviceFragment = SearchDeviceFragment()

            if (!stateResolver.getWifiState() || !stateResolver.getLocationState() || !stateResolver.getBluetoothState()){

                currentFragment = StateResolverFragment()
            }else{

            currentFragment = searchDeviceFragment

            }

        }else{

            val tag = savedInstanceState.getString("curr")
            currentFragment = supportFragmentManager.findFragmentByTag(tag) as BaseFragment
        }

        supportFragmentManager.apply {

            addFragmentOnAttachListener { fragmentManager, fragment ->

                currentFragment = fragment as BaseFragment
            }

            addOnBackStackChangedListener {

                currentFragment = supportFragmentManager.findFragmentById(R.id.activity_receive_navigator)!! as BaseFragment

            }

            commit {

                replace(R.id.activity_receive_navigator,currentFragment,currentFragment.getTAG())
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("curr",currentFragment.getTAG())
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0){

            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }else{
            super.onBackPressed()
        }
    }
    override fun onFragment(fragment: Fragment) {

    }

    override fun onFragmentVisibility(fragment: Fragment) {

    }
}