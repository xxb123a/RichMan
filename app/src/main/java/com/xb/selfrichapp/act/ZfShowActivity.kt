package com.xb.selfrichapp.act

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xb.selfrichapp.R
import com.xb.selfrichapp.manager.WorkModeManager

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

    private val mAdapter by lazy { ZfAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zf_list)
        findViewById<View>(R.id.btn_refresh).setOnClickListener {
            WorkModeManager.refreshZf(true) {
                mAdapter.refresh()
            }
        }
        val rv = findViewById<RecyclerView>(R.id.rv_zf)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mAdapter
    }


    class ItemZfViewHolder(view: View) : ViewHolder(view)

    inner class ZfAdapter : Adapter<ItemZfViewHolder>() {
        private var mZfList = WorkModeManager.getBattleMode()
        fun refresh() {
            mZfList = WorkModeManager.getBattleMode()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemZfViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_zf, parent, false)
            return ItemZfViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mZfList.size
        }

        override fun onBindViewHolder(holder: ItemZfViewHolder, position: Int) {
            val data = mZfList[position]
            (holder.itemView as TextView).text = data.name
            holder.itemView.setOnClickListener { TextShowActivity.launch(this@ZfShowActivity,data.getShowTitle(),data.getShowContent()) }
        }
    }
}