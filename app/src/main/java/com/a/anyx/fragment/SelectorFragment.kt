package com.a.anyx.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.a.anyx.R
import com.a.anyx.fragment.base.BaseFragment
import com.a.anyx.fragment.dialog.WhatFileFragment
import com.a.anyx.util.StateResolver
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView

class SelectorFragment : BaseFragment() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var sendButton: Button

    private lateinit var visibleFragment: BaseFragment
    private var visibleFragmentHidden:Boolean = false
    private var currentNavTabId: Int = 0

    private lateinit var imageFragment: ImageFragment
    private lateinit var videoFragment: VideoFragment
    private lateinit var songFragment: SongFragment
    private lateinit var fileFragment: FileFragment

    private var totalSelectedFilePaths: Int = -1
    private lateinit var numContent: TextView

    override fun getTAG(): String? {
        return TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_selector, container, false)
    }

    override fun getSelectedFilePathItems(): ArrayList<String>? {
        return arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {

            totalSelectedFilePaths = 0

            imageFragment = ImageFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2, it, ImageFragment.TAG).hide(it)
                }
            }

            videoFragment = VideoFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2, it, VideoFragment.TAG).hide(it)
                }
            }

            songFragment = SongFragment().also {

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2, it, SongFragment.TAG).hide(it)
                }
            }

            fileFragment = FileFragment().also {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragmentContainerView2, it, FileFragment.TAG).hide(it)
                }
            }

            visibleFragment = imageFragment
            currentNavTabId = R.id.imageFragment

        } else {

            totalSelectedFilePaths = savedInstanceState.getInt(TOTAL_SELECTION)

            imageFragment = childFragmentManager.findFragmentByTag(ImageFragment.TAG) as ImageFragment
            videoFragment = childFragmentManager.findFragmentByTag(VideoFragment.TAG) as VideoFragment
            songFragment = childFragmentManager.findFragmentByTag(SongFragment.TAG) as SongFragment
            fileFragment = childFragmentManager.findFragmentByTag(FileFragment.TAG) as FileFragment

            currentNavTabId = savedInstanceState.getInt("prev")

            visibleFragment = when (currentNavTabId) {
                R.id.imageFragment -> {
                    imageFragment
                }
                R.id.videoFragment -> {
                    videoFragment
                }
                R.id.songFragment -> {
                    songFragment
                }
                R.id.fileFragment -> {
                    fileFragment
                }
                else -> {
                    return
                }
            }

            visibleFragmentHidden = savedInstanceState.getBoolean(VISIBLE)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         *val navController = requireActivity().findNavController(R.id.fragmentContainerView2)
         *val parentNavController = requireActivity().findNavController(R.id.fragmentContainerView)
         *NavigationUI.setupWithNavController(bottomNavigationView,navController)
         */

        numContent = view.findViewById<TextView?>(R.id.fragment_selector_num_of_content).apply {

            text = "$totalSelectedFilePaths"
        }

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {

            setNavigationOnClickListener {

                requireActivity().onBackPressed()
            }

            menu.clear()

            inflateMenu(R.menu.item_options)


            setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {

                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    when (item?.itemId) {

                        R.id.sort_by->{

                            menu.clear()
                            inflateMenu(R.menu.sort_by_menu)


                            return true
                        }

                        R.id.sort_date_newest -> {

                            visibleFragment.sortList(
                                SelectorFragment.SortType.SORT_TYPE_DATE,
                                false
                            )
                            return true
                        }

                        R.id.sort_date_oldest -> {

                            visibleFragment.sortList(SelectorFragment.SortType.SORT_TYPE_DATE, true)

                            return true
                        }

                        R.id.sort_length_largest -> {

                            visibleFragment.sortList(
                                SelectorFragment.SortType.SORT_TYPE_LENGTH,
                                false
                            )

                            return true
                        }

                        R.id.sort_length_smallest -> {

                            visibleFragment.sortList(
                                SelectorFragment.SortType.SORT_TYPE_LENGTH,
                                true
                            )

                            return true
                        }

                        R.id.sort_a_z -> {

                            visibleFragment.sortList(
                                SelectorFragment.SortType.SORT_TYPE_NAME,
                                false
                            )

                            return true
                        }

                        R.id.sort_z_a -> {

                            visibleFragment.sortList(SelectorFragment.SortType.SORT_TYPE_NAME, true)

                            return true
                        }

                        else -> {
                            return false
                        }
                    }
                }
            })
        }


        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)

        if (!visibleFragmentHidden)childFragmentManager.beginTransaction().show(visibleFragment).commit()

        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.imageFragment -> {

                    childFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                        hide(visibleFragment)
                        show(imageFragment)
                    }

                    visibleFragment = imageFragment
                    currentNavTabId = item.itemId
                }

                R.id.videoFragment -> {

                    childFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                        hide(visibleFragment)
                        show(videoFragment)
                    }

                    visibleFragment = videoFragment
                    currentNavTabId = item.itemId
                }

                R.id.songFragment -> {

                    childFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                        hide(visibleFragment)
                        show(songFragment)
                    }

                    visibleFragment = songFragment
                    currentNavTabId = item.itemId
                }

                R.id.fileFragment -> {

                   childFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_in_bottom_enter,R.anim.simple_fade_out,R.anim.simple_fade_in,R.anim.slide_out_bottom_exit)

                        hide(visibleFragment)
                        show(fileFragment)
                    }

                    visibleFragment = fileFragment
                    currentNavTabId = item.itemId
                }
            }

            true
        }


        sendButton = view.findViewById<Button?>(R.id.fragment_selector_send).apply {

            isEnabled = totalSelectedFilePaths > 0

            val continueSend = requireContext().getString(R.string.continue_send)
            val files = requireContext().getString(R.string.files)

            setOnClickListener {

                MaterialAlertDialogBuilder(requireContext()).setTitle("$continueSend $totalSelectedFilePaths $files ?")
                    .setPositiveButton(R.string.yes,object :DialogInterface.OnClickListener{

                        override fun onClick(dialog: DialogInterface?, which: Int) {

                            val stateResolver = StateResolver(requireContext())

                            nextFragment(!stateResolver.getLocationState() || !stateResolver.getWifiState() || !stateResolver.getBluetoothState())

                        }
                    })
                    .setNegativeButton(R.string.no,object :DialogInterface.OnClickListener{

                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    }).show()
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt("prev", currentNavTabId)
        outState.putInt(TOTAL_SELECTION, totalSelectedFilePaths)
        outState.putBoolean(VISIBLE,visibleFragment.isHidden)

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed(): Boolean {

        return visibleFragment.onBackPressed()
    }

    override fun loadImage(imageView: ImageView, position: Int) {

    }

    private fun nextFragment(statesRight: Boolean) {

        if (statesRight) {

            var stateResolverFragment =
                requireActivity().supportFragmentManager.findFragmentByTag(
                    StateResolverFragment.TAG
                )

            if (stateResolverFragment == null)
                stateResolverFragment = StateResolverFragment()

            requireActivity().supportFragmentManager.commit {

                setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim
                )

                addToBackStack(null)

                replace(
                    R.id.activity_send_navigator,
                    stateResolverFragment,
                    StateResolverFragment.TAG
                )
            }

        } else {

            var waitDeviceFragment =
                requireActivity().supportFragmentManager.findFragmentByTag(
                    WaitDeviceFragment.TAG
                )

            val paths = arrayListOf<String>().apply {

                addAll(imageFragment.getSelectedFilePathItems()!!)
                addAll(videoFragment.getSelectedFilePathItems()!!)
                addAll(songFragment.getSelectedFilePathItems()!!)
                addAll(fileFragment.getSelectedFilePathItems()!!)
            }

            if (waitDeviceFragment == null) {
                waitDeviceFragment = WaitDeviceFragment()

                waitDeviceFragment.arguments = Bundle().apply {

                    putStringArrayList(TransferFragment.FILE_PATHS, paths)
                }
            }

            requireActivity().supportFragmentManager.commit {

                setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim
                )
                addToBackStack(null)
                replace(
                    R.id.activity_send_navigator,
                    waitDeviceFragment,
                    SearchDeviceFragment.TAG
                )
            }

        }
    }

    override fun sortList(sortType: SortType, desc: Boolean) {

        visibleFragment.sortList(sortType, desc)
    }

    fun updateSelectionCount() {

        val totalSelection = imageFragment.getSelectedFilePathItems()?.size!! +
                videoFragment.getSelectedFilePathItems()?.size!! +
                songFragment.getSelectedFilePathItems()?.size!! +
                fileFragment.getSelectedFilePathItems()?.size!!

        totalSelectedFilePaths = totalSelection

        numContent.text = "$totalSelection"
        sendButton.isEnabled = totalSelectedFilePaths > 0

    }

    enum class SortType {

        SORT_TYPE_DATE,
        SORT_TYPE_LENGTH,
        SORT_TYPE_NAME
    }

    companion object {

        @JvmField
        val TAG = SelectorFragment::class.simpleName

        const val IMAGES_DATA = "images_data"
        const val VIDEOS_DATA = "videos_data"
        const val SONGS_DATA = "songs_data"
        const val FILES_DATA = "files_data"

        const val TOTAL_SELECTION = "total_selection"
        const val VISIBLE = "visible"
    }


}