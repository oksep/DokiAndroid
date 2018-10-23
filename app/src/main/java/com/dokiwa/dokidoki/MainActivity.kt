package com.dokiwa.dokidoki

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.dokiwa.dokidoki.admin.ADMIN_MODULE_NAME
import com.dokiwa.dokidoki.center.CENTER_MODULE_NAME

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text).text = CENTER_MODULE_NAME + ADMIN_MODULE_NAME + packageName
    }
}
