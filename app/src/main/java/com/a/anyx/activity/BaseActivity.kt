package com.a.anyx.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.elevation.SurfaceColors

abstract class BaseActivity : AppCompatActivity() {

    @ColorInt
    private var surfaceElevationColor:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        surfaceElevationColor = SurfaceColors.SURFACE_2.getColor(this@BaseActivity)

        window.apply {

            navigationBarColor = surfaceElevationColor
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        super.onCreate(savedInstanceState)
    }

}