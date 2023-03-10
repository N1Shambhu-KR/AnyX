package com.a.anyx.activity

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.a.anyx.R
import com.a.anyx.fragment.*
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.util.StateResolver

class ReceiveActivity:BaseActivity(),IOnFragment{

    private lateinit var currentFragment: BaseFragment

    private lateinit var searchDeviceFragment: SearchDeviceFragment

    private lateinit var stateResolver:StateResolver

    private lateinit var stateResolverFragment: StateResolverFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        stateResolver = StateResolver(this)

        if (savedInstanceState == null){

            if (!stateResolver.getWifiState() || !stateResolver.getLocationState() || !stateResolver.getBluetoothState()){

                stateResolverFragment = StateResolverFragment()
                currentFragment = stateResolverFragment
            }else{

                searchDeviceFragment = SearchDeviceFragment()
            currentFragment = searchDeviceFragment

            }

        }else{

            val tag = savedInstanceState.getString("curr")
            currentFragment = supportFragmentManager.findFragmentByTag(tag) as BaseFragment
        }

        supportFragmentManager.apply {

            addFragmentOnAttachListener { fragmentManager, fragment ->

                if (fragment is BaseFragment)
                currentFragment = fragment as BaseFragment

                Toast.makeText(this@ReceiveActivity,currentFragment.getTAG(),Toast.LENGTH_SHORT).show()
            }

            addOnBackStackChangedListener {

                currentFragment = supportFragmentManager.findFragmentById(R.id.activity_receive_navigator)!! as BaseFragment
                Toast.makeText(this@ReceiveActivity,currentFragment.getTAG(),Toast.LENGTH_SHORT).show()

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

        if (currentFragment.onBackPressed()){

            if (supportFragmentManager.backStackEntryCount > 0){

                supportFragmentManager.popBackStackImmediate()
                if (currentFragment is StateResolverFragment)
                    onBackPressed()
            }else{
                super.onBackPressed()
            }
        }
    }
    override fun onFragment(fragment: Fragment) {

    }

    override fun onFragmentVisibility(fragment: Fragment) {

    }
}