package com.a.anyx.util

import android.content.Context
import android.util.TypedValue

class DisplayUtils() {

    companion object{

        fun dp2pixel(context: Context,dp:Float):Float{
             return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.resources.displayMetrics)
        }
    }
}