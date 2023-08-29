package com.xb.selfrichapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xb.selfrichapp.act.ZfShowActivity

class MainActivity : AppCompatActivity() {
    //https://raw.githubusercontent.com/xxb123a/RichMan/main
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.ib_setting).setOnClickListener {
            ZfShowActivity.launch(this)
        }
    }
}