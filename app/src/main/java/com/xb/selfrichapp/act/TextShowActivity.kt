package com.xb.selfrichapp.act

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.R

/**
 *_    .--,       .--,
 *_   ( (  \\.---./  ) )
 *_    '.__/o   o\\__.'
 *_       {=  ^  =}
 *_        >  -  <
 *_       /       \\
 *_      //       \\\\
 *_     //|   .   |\\\\
 *_     \"'\\       /'\"_.-~^`'-.
 *_        \\  _  /--'         `
 *_      ___)( )(___
 *_     (((__) (__)))    高山仰止,景行行止.虽不能至,心向往之。
 * author      : xue
 * date        : 2023/8/29 08:42
 * description :
 */
class TextShowActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: Activity, title: String, content: String) {
            activity.startActivity(Intent(activity,TextShowActivity::class.java)
                .putExtra("title",title)
                .putExtra("content",content))
        }
    }
    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val mContent by lazy { intent.getStringExtra("content") ?: "" }
    private val mTitleTv by lazy { findViewById<TextView>(R.id.tv_title) }
    private val mContentTv by lazy { findViewById<TextView>(R.id.tv_content) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_show)
        mTitleTv.text = mTitle
        mContentTv.text = mContent
    }
}