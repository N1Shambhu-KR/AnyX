package com.a.anyx

import android.app.Application
import android.database.sqlite.SQLiteCursor
import com.google.android.material.color.DynamicColors

class AnyXApp:Application() {

    init {
       DynamicColors.applyToActivitiesIfAvailable(this)
    }


}