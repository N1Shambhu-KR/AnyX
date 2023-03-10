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

class SendActivity: BaseActivity(),IOnFragment{

    private lateinit var currentFragment: BaseFragment

    private lateinit var selectorFragment: SelectorFragment

    private lateinit var stateResolver: StateResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        stateResolver = StateResolver(this)

        if (savedInstanceState == null){

            selectorFragment = SelectorFragment()

                currentFragment = selectorFragment

        }else{

            val tag = savedInstanceState.getString("curr")
            currentFragment = supportFragmentManager.findFragmentByTag(tag) as BaseFragment
        }

        supportFragmentManager.apply {

            addFragmentOnAttachListener { fragmentManager, fragment ->

                if (fragment is BaseFragment)
                currentFragment = fragment

                //Toast.makeText(this@SendActivity,currentFragment.getTAG(),Toast.LENGTH_SHORT).show()
            }


            addOnBackStackChangedListener {

                currentFragment = supportFragmentManager.findFragmentById(R.id.activity_send_navigator)!! as BaseFragment

                //Toast.makeText(this@SendActivity,currentFragment.getTAG(), Toast.LENGTH_SHORT).show()

            }

            commit{

                replace(R.id.activity_send_navigator,currentFragment,currentFragment.getTAG())
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("curr",currentFragment.getTAG())
        super.onSaveInstanceState(outState)
    }

    override fun onFragment(fragment: Fragment) {

    }

    override fun onFragmentVisibility(fragment: Fragment) {

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
}