package com.a.anyx.util

import java.text.StringCharacterIterator

class DataUtils {

    companion object{

        fun bytesToReadableFormat(bytes:Long):String{

            val suffixes = arrayOf("B","KB","MB","GB","TB","PB")

            var index = 0

            var size = bytes.toDouble()

            while (size>=1000 && index < suffixes.size-1){

                size/=1000
                index++
            }

            return String.format("%.2f %s",size,suffixes[index])
        }
    }
}