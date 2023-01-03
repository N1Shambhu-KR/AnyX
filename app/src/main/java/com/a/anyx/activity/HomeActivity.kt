package com.a.anyx.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.a.anyx.AppConstants
import com.a.anyx.R
import com.a.anyx.util.PermissionChecker

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val pc = PermissionChecker(this)

        findViewById<Button>(R.id.activity_home_send_action).apply {

            setOnClickListener {

                if (!(pc.hasStoragePermission() && pc.hasLocationPermission())){
                    val intentionalIntent = Intent(this@HomeActivity, PermissionActivity::class.java)
                    intentionalIntent.putExtra(AppConstants.INTENT_CLASS, SendActivity::class.qualifiedName)
                    startActivity(intentionalIntent)

                }else{

                    startActivity(Intent(this@HomeActivity, SendActivity::class.java))
                }

            }
        }

        findViewById<Button>(R.id.activity_home_receive_action).apply {

            setOnClickListener {

                if (!(pc.hasStoragePermission() && pc.hasLocationPermission())){
                    val intentionalIntent = Intent(this@HomeActivity, PermissionActivity::class.java)
                    intentionalIntent.putExtra(AppConstants.INTENT_CLASS, SendActivity::class.qualifiedName)
                    startActivity(intentionalIntent)

                }else{

                    startActivity(Intent(this@HomeActivity, ReceiveActivity::class.java))
                }
            }
        }
    }
}