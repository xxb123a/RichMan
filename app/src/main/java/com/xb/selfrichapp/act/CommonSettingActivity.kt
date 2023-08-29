package com.xb.selfrichapp.act

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
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
 * date        : 2023/8/29 16:46
 * description :
 */
class CommonSettingActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, CommonSettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_set)
        commonClick(R.id.btn_zf_list) {
            ZfShowActivity.launch(this)
        }
        commonClick(R.id.btn_date_setting) {
            DaySettingActivity.launch(this)
        }
    }

    private fun commonClick(@IdRes id: Int, callback: () -> Unit) {
        findViewById<View>(id).setOnClickListener { callback.invoke() }
    }
}