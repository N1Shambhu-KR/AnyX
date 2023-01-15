package com.a.anyx.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.a.anyx.R
import com.a.anyx.fragment.*
import com.a.anyx.interfaces.IOnFragment
import com.a.anyx.util.StateResolver

class SendActivity: BaseActivity(),IOnFragment{

    private lateinit var currentFragment:BaseFragment

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
            }


            commit {

                replace(R.id.activity_send_navigator,currentFragment,currentFragment.getTAG())
            }

            addOnBackStackChangedListener {

                currentFragment = supportFragmentManager.findFragmentById(R.id.activity_send_navigator)!! as BaseFragment
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

        if (currentFragment.onBackPressed()) super.onBackPressed()
    }
}