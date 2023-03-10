package com.a.anyx.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.a.anyx.activity.PermissionActivity
import java.lang.Exception

class PermissionChecker(private val activity: Activity) {

    fun hasStoragePermission():Boolean{

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){

            Environment.isExternalStorageManager()
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }else{
            return true
        }
    }

    fun hasLocationPermission():Boolean{

        return ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    fun requestStoragePermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){

            try {

                val storageIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                storageIntent.addCategory(Intent.CATEGORY_DEFAULT)
                storageIntent.setData(Uri.parse("package:${activity.applicationContext.packageName}"))
                activity.startActivityForResult(storageIntent,
                    PermissionActivity.STORAGE_PERMISSION_CODE
                )

            }catch (e: Exception){

                val storageIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(storageIntent,
                    PermissionActivity.STORAGE_PERMISSION_CODE
                )
            }


        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || !activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionActivity.STORAGE_PERMISSION_CODE
                    )
                }
        }
    }

    fun requestLocationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PermissionActivity.LOCATION_PERMISSION_CODE
                )
            }

        }else{

            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionActivity.LOCATION_PERMISSION_CODE
            )
        }

    }

}