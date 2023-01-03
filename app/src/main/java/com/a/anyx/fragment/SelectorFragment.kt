package com.a.anyx.fragment

import android.os.*
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.commit

import com.a.anyx.R
import com.a.anyx.util.StateResolver
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class SelectorFragment : BaseFragment(){

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var button: Button

    private lateinit var visibleFragment: BaseFragment
    private var currentNavTabId:Int = 0

    private lateinit var imageFragment: ImageFragment
    private lateinit var videoFragment: VideoFragment
    private lateinit var songFragment: SongFragment
    private lateinit var fileFragment: FileFragment

    override fun getTAG(): String? {
        return TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_selector,container,false)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return null
    }

    override fun onBackPressed() {

    }

    override fun onPermissionChanged() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null){

            imageFragment = ImageFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2,it,ImageFragment.TAG).hide(it)
                }
            }

            videoFragment = VideoFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2,it,VideoFragment.TAG).hide(it)
                }
            }

            songFragment = SongFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2,it,SongFragment.TAG).hide(it)
                }
            }

            fileFragment = FileFragment().also {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2,it,FileFragment.TAG).hide(it)
                }
            }

            visibleFragment = imageFragment
            currentNavTabId = R.id.imageFragment

        }else{
            imageFragment = childFragmentManager.findFragmentByTag(ImageFragment.TAG) as ImageFragment
            videoFragment = childFragmentManager.findFragmentByTag(VideoFragment.TAG) as VideoFragment
            songFragment = childFragmentManager.findFragmentByTag(SongFragment.TAG) as SongFragment
            fileFragment = childFragmentManager.findFragmentByTag(FileFragment.TAG) as FileFragment

            currentNavTabId = savedInstanceState.getInt("prev")

            visibleFragment = when(currentNavTabId){
                R.id.imageFragment-> {imageFragment}
                R.id.videoFragment-> {videoFragment}
                R.id.songFragment->{songFragment}
                R.id.fileFragment->{fileFragment}
                else->{return}
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         *val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
         *val parentNavController = requireActivity().findNavController(R.id.fragmentContainerView)
         *NavigationUI.setupWithNavController(bottomNavigationView,navController)
         */

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)

        childFragmentManager.beginTransaction().show(visibleFragment).commit()

        bottomNavigationView.setOnItemSelectedListener(object :NavigationBarView.OnItemSelectedListener {

            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                when(item.itemId){
                    R.id.imageFragment->{

                        childFragmentManager.commit {
                            hide(visibleFragment)
                            show(imageFragment)
                        }

                        visibleFragment = imageFragment
                        currentNavTabId = item.itemId
                    }

                    R.id.videoFragment->{

                        childFragmentManager.commit {
                            hide(visibleFragment)
                            show(videoFragment)
                        }

                        visibleFragment = videoFragment
                        currentNavTabId = item.itemId
                    }

                    R.id.songFragment->{

                        childFragmentManager.commit {
                            hide(visibleFragment)
                            show(songFragment)
                        }

                        visibleFragment = songFragment
                        currentNavTabId = item.itemId
                    }

                    R.id.fileFragment->{

                        childFragmentManager.commit {
                            hide(visibleFragment)
                            show(fileFragment)
                        }

                        visibleFragment = fileFragment
                        currentNavTabId = item.itemId
                    }
                }

                return true
            }
         })

        button = view.findViewById<Button?>(R.id.activity_main_send).apply {
            setOnClickListener {

                val stateResolver = StateResolver(requireContext())


                if (!stateResolver.getLocationState() || !stateResolver.getWifiState() || !stateResolver.getBluetoothState()){

                    var stateResolverFragment = requireActivity().supportFragmentManager.findFragmentByTag(StateResolverFragment.TAG)

                    if (stateResolverFragment == null)
                        stateResolverFragment = StateResolverFragment()

                    requireActivity().supportFragmentManager.commit {

                        addToBackStack(null)
                        replace(R.id.activity_send_navigator,stateResolverFragment,StateResolverFragment.TAG)
                    }

                }else{

                    var ownerDeviceFragment = requireActivity().supportFragmentManager.findFragmentByTag(OwnerDeviceFragment.TAG)

                    if (ownerDeviceFragment == null)
                        ownerDeviceFragment = OwnerDeviceFragment()

                    ownerDeviceFragment.arguments = Bundle().apply {

                        putStringArrayList(IMAGES_DATA,imageFragment.getSelectedFilePathItems())
                        putStringArrayList(VIDEOS_DATA,videoFragment.getSelectedFilePathItems())
                        putStringArrayList(SONGS_DATA, songFragment.getSelectedFilePathItems())
                        putStringArrayList(FILES_DATA,fileFragment.getSelectedFilePathItems())
                    }

                    requireActivity().supportFragmentManager.commit {

                        addToBackStack(null)
                        replace(R.id.activity_send_navigator,ownerDeviceFragment,OwnerDeviceFragment.TAG)
                    }

                }

            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt("prev",currentNavTabId)

        super.onSaveInstanceState(outState)
    }

     companion object{

        @JvmField val TAG = SelectorFragment::class.simpleName

         const val IMAGES_DATA = "images_data"
         const val VIDEOS_DATA = "videos_data"
         const val SONGS_DATA = "songs_data"
         const val FILES_DATA = "files_data"
    }


}