package com.a.anyx.content

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class ContentStore(private val applicationContext: Context){

    private val imageData:MutableList<ContentData> = mutableListOf()
    private val videoData:MutableList<ContentData> = mutableListOf()
    private val songData:MutableList<ContentData> = mutableListOf()

    fun getImageData():MutableList<ContentData> = imageData
    fun getVideoData():MutableList<ContentData> = videoData
    fun getSongData():MutableList<ContentData> = songData

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
                var mimeType = "N/A"
                applicationContext.contentResolver.getType(uri)?.also {
                    mimeType = it
                }

                imageData.add(ContentData(name, uri, path, size, date,mimeType))
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
                var mimeType = "N/A"
                applicationContext.contentResolver.getType(uri)?.also {
                    mimeType = it
                }

                videoData.add(ContentData(name, uri, path, size, date,mimeType))
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
                var mimeType = "N/A"
                applicationContext.contentResolver.getType(uri)?.also {
                    mimeType = it
                }

                songData.add(ContentData(name, uri, path, size, date,mimeType))
            }
        }
        cursor.close()

    }



}