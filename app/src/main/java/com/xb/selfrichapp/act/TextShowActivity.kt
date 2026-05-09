package com.xb.selfrichapp.act

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.R
import com.xb.selfrichapp.tool.ToastTools

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
        fun launch(activity: Activity, title: String, content: String,delayClose: Boolean = false) {
            activity.startActivity(Intent(activity,TextShowActivity::class.java)
                .putExtra("title",title)
                .putExtra("content",content)
                .putExtra("delay",delayClose)
            )
        }
    }
    private val mTitle by lazy { intent.getStringExtra("title") ?: "" }
    private val mContent by lazy { intent.getStringExtra("content") ?: "" }
    private val delayClose by lazy { intent.getBooleanExtra("delay",false) }
    private val mTitleTv by lazy { findViewById<TextView>(R.id.tv_title) }
    private val mContentTv by lazy { findViewById<TextView>(R.id.tv_content) }
    private var closeEnable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeEnable = !delayClose
        setContentView(R.layout.activity_text_show)
        mTitleTv.text = mTitle
        mContentTv.text = mContent
        mTitleTv.postDelayed({
            closeEnable = true
        },5000)
    }

    override fun onBackPressed() {
        if(!closeEnable) {
            ToastTools.showText("请认真阅读，时间未到不能关闭")
            return
        }
        super.onBackPressed()
    }
}