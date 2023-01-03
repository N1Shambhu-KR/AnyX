package com.a.anyx.util

import android.net.Uri
import com.a.anyx.content.ContentData
import java.io.File
import java.util.ArrayList

class FileIterator(private val file: File) {

    fun iterate():ArrayList<ContentData>{

        val fileArray = ArrayList<ContentData>()

        if (file.exists() && file.isDirectory){

            for (f in file.listFiles()){

                fileArray.add(ContentData(f.name, Uri.fromFile(f),f.absolutePath,f.length(),f.lastModified()))
            }
        }

        return fileArray
    }
}