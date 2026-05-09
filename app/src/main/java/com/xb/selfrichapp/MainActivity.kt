package com.xb.selfrichapp

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.act.CommonSettingActivity
import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.manager.WorkModeManager
import com.xb.selfrichapp.prefs.Prefs

class MainActivity : AppCompatActivity() {

    private var mLastLoadTime = 0L
    private val mMainView by lazy { findViewById<View>(R.id.main_content) }
    private val mTvContent by lazy { findViewById<TextView>(R.id.tv_content) }
    private var mDayData: DayDataEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        loadMainData()
    }


    private fun loadMainData() {
        val time = Prefs.mShowDataTime
        if (mLastLoadTime == time) return
        mLastLoadTime = time
        if (mLastLoadTime == 0L) return
        WorkModeManager.getDayData(mLastLoadTime, false) {
            showContent(it)
        }
    }

    private fun showContent(dde: DayDataEntity) {
        if (dde.date.isEmpty()) {
            mDayData = null
            mTvContent.text = "暂时无数据"
            mMainView.setBackgroundColor(Color.WHITE)
            mTvContent.setTextColor(Color.BLACK)
            return
        }

        mTvContent.text = dde.toShowText()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, R.id.main_setting, 0, "设置")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_setting -> {
                CommonSettingActivity.launch(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}