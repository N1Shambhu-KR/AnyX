package com.a.anyx.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.a.anyx.content.ContentData
import com.a.anyx.content.ContentStore
import com.a.anyx.fragment.SelectorFragment
import java.util.*
import java.util.concurrent.Executors

class ContentViewModel(app:Application):AndroidViewModel(app) {

    @Volatile
    private var contentData = MutableLiveData<MutableList<ContentData>>()

    private var store: ContentStore = ContentStore(app)

    val data:LiveData<MutableList<ContentData>>
    get() = contentData

    fun loadData(which:String) {

        Executors.newSingleThreadExecutor().execute {

            when(which){

                SelectorFragment.IMAGES_DATA ->{

                    store.collectImages()
                    //contentData.postValue(store.getImageData())
                }

                SelectorFragment.VIDEOS_DATA ->{

                    store.collectVideos()
                    //contentData.postValue(store.getVideoData())
                }

                SelectorFragment.SONGS_DATA->{

                    store.collectSongs()
                    //contentData.postValue(store.getSongData())
                }

                else->{

                    Log.d(TAG,"$which is invalid")
                }
            }
        }

        when(which){

            SelectorFragment.IMAGES_DATA ->{

               // store.collectImages()
                contentData.postValue(store.getImageData())
            }

            SelectorFragment.VIDEOS_DATA ->{

                //store.collectVideos()
                contentData.postValue(store.getVideoData())
            }

            SelectorFragment.SONGS_DATA->{

               // store.collectSongs()
                contentData.postValue(store.getSongData())
            }

            else->{

                Log.d(TAG,"$which is invalid")
            }
        }
    }


    fun sort(type:SelectorFragment.SortType,desc: Boolean) {

        Executors.newSingleThreadExecutor().execute {

            val sorted = data.value?.sortedWith { o1, o2 ->

                when (type) {

                    SelectorFragment.SortType.SORT_TYPE_NAME -> {

                        if (!desc) {
                            o1?.name!!.compareTo(o2?.name!!)
                        } else {
                            o2?.name!!.compareTo(o1?.name!!)
                        }
                    }

                    SelectorFragment.SortType.SORT_TYPE_DATE -> {

                        if (desc) {
                            o1?.date!!.compareTo(o2?.date!!)
                        } else {
                            o2?.date!!.compareTo(o1?.date!!)
                        }
                    }

                    SelectorFragment.SortType.SORT_TYPE_LENGTH -> {

                        if (desc) {
                            o1?.length!!.compareTo(o2?.length!!)
                        } else {
                            o2?.length!!.compareTo(o1?.length!!)
                        }
                    }

                    else -> {
                        -1
                    }
                }
            }

            contentData.postValue(sorted as MutableList<ContentData>)
        }

    }

    companion object{

        @JvmField val TAG:String = ContentViewModel::class.simpleName!!
    }
}