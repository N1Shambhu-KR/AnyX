package com.a.anyx.content

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class ContentStore(private val applicationContext: Context){

    private val imageData = ArrayList<ContentData>()
    private val videoData = ArrayList<ContentData>()
    private val songData = ArrayList<ContentData>()

    fun getImageData():ArrayList<ContentData> = imageData
    fun getVideoData():ArrayList<ContentData> = videoData
    fun getSongData():ArrayList<ContentData> = songData

    fun collectImages(){

        val projection = arrayOf(MediaStore.MediaColumns._ID,MediaStore.MediaColumns.DISPLAY_NAME,MediaStore.MediaColumns.DATA
                                 ,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.MediaColumns.SIZE)

        val cursor = applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,null,null,null,null)

        cursor!!.moveToFirst().let {

            while (cursor.moveToNext()){

                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                imageData.add(ContentData(name,uri,path,size,date))
            }
        }
        cursor.close()

    }

    fun collectVideos(){

        val projection = arrayOf(MediaStore.MediaColumns._ID,MediaStore.MediaColumns.DISPLAY_NAME,MediaStore.MediaColumns.DATA
            ,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.MediaColumns.SIZE)

        val cursor = applicationContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null,null)

        cursor!!.moveToFirst().let {

            while (cursor.moveToNext()){

                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id)

                videoData.add(ContentData(name,uri,path,size,date))
            }
        }
        cursor.close()

    }

    fun collectSongs(){

        val projection = arrayOf(MediaStore.MediaColumns._ID,MediaStore.MediaColumns.DISPLAY_NAME,MediaStore.MediaColumns.DATA
            ,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.MediaColumns.SIZE)

        val cursor = applicationContext.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null,null)

        cursor!!.moveToFirst().let {

            while (cursor.moveToNext()){

                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)

                songData.add(ContentData(name,uri,path,size, date))
            }
        }
        cursor.close()

    }



}