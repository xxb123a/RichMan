package com.xb.selfrichapp.act

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
 * date        : 2023/8/29 08:50
 * description :
 */
class ZfShowActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ZfShowActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zf_list)
        findViewById<View>(R.id.btn_refresh).setOnClickListener {
        }
    }


    class ItemZfViewHolder(view: View) : ViewHolder(view)

}