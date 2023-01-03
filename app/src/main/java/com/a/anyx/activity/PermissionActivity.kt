package com.a.anyx.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.a.anyx.AppConstants
import com.a.anyx.R
import com.a.anyx.util.PermissionChecker

class PermissionActivity : AppCompatActivity() {

    private lateinit var storageAccess:Button
    private lateinit var locationAccess:Button
    private lateinit var nearByDevicesAccess:Button

    private lateinit var permissionChecker: PermissionChecker

    private lateinit var letsGo:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        permissionChecker = PermissionChecker(this)

        if (Build.VERSION.SDK_INT >= 33){

            val nearbyPermissionView = layoutInflater.inflate(R.layout.nearby_permission_view,window.findViewById(R.id.activity_permission_root),false)
            findViewById<FrameLayout>(R.id.activity_permission_nearby_permission_holder).apply {
                addView(nearbyPermissionView)
            }

            setUpNearbyDeicesButton()
            setUpPermissionButtons()
        }else{

            setUpPermissionButtons()
        }
    }

    private fun setUpNearbyDeicesButton(){

        nearByDevicesAccess = findViewById<Button?>(R.id.activity_permission_access_nearby_devices).apply {

        }
    }

    private fun setUpPermissionButtons(){

        storageAccess = findViewById<Button?>(R.id.activity_permission_access_storage).apply {
            setOnClickListener {
                permissionChecker.requestStoragePermission()
            }
        }

        locationAccess = findViewById<Button?>(R.id.activity_permission_access_location).apply {
            setOnClickListener {
                permissionChecker.requestLocationPermission()
            }
        }

        letsGo = findViewById<Button?>(R.id.activity_permission_lets_go).apply {
          //  isEnabled = false
            setOnClickListener {
                val forwardClassComponent = intent.getStringExtra(AppConstants.INTENT_CLASS)

                startActivity(Intent(this@PermissionActivity, Class.forName(forwardClassComponent)))
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            LOCATION_PERMISSION_CODE ->{

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (permissionChecker.hasStoragePermission())
                        letsGo.isEnabled = true
                }
            }

            STORAGE_PERMISSION_CODE ->{

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (permissionChecker.hasLocationPermission())
                        letsGo.isEnabled = true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        @RequiresApi(32)
        if (requestCode == STORAGE_PERMISSION_CODE ){

            if (Environment.isExternalStorageManager()){

                if (permissionChecker.hasLocationPermission())
                    letsGo.isEnabled = true
            }
        }

    }

    companion object{

        val STORAGE_PERMISSION_CODE = 101
        val LOCATION_PERMISSION_CODE = 100
        val NEARBY_DEVICES_PERMISSION = 102
    }

}
