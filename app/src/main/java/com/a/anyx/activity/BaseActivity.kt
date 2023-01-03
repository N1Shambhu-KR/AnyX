package com.a.anyx.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.elevation.SurfaceColors

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.apply {
            navigationBarColor = SurfaceColors.SURFACE_2.getColor(this@BaseActivity)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        super.onCreate(savedInstanceState)
    }

}