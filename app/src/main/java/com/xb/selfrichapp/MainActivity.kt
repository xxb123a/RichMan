package com.xb.selfrichapp

import android.app.Activity
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.act.CommonSettingActivity
import com.xb.selfrichapp.act.DataThinkActivity
import com.xb.selfrichapp.act.TextShowActivity
import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.manager.WorkModeManager
import com.xb.selfrichapp.prefs.Prefs
import com.xb.selfrichapp.tool.ToastTools

class MainActivity : AppCompatActivity() {

    private var mLastLoadTime = 0L
    private val mMainView by lazy { findViewById<View>(R.id.main_content) }
    private val mTvContent by lazy { findViewById<TextView>(R.id.tv_content) }
    private var mDayData: DayDataEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparencyBar()
        setContentView(R.layout.activity_main)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if(hour == 9){
            showCoreThink(true)
        }
        commonClick(R.id.data_set){
            CommonSettingActivity.launch(this)
        }
        commonClick(R.id.core_think){
            showCoreThink(false)
        }
        commonClick(R.id.data_think){
            startDataThink()
        }
    }
    private fun commonClick(@IdRes id: Int, callback: () -> Unit) {
        findViewById<View>(id).setOnClickListener { callback.invoke() }
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
        menu?.add(1, R.id.main_core_think, 1, "核心思想")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        menu?.add(2, R.id.main_data_think, 1, "数据分析")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_setting -> {
                CommonSettingActivity.launch(this)
            }
            R.id.main_core_think -> {
                showCoreThink(false)
            }
            R.id.main_data_think -> {
                startDataThink()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCoreThink(delay: Boolean){
        WorkModeManager.getCoreThink(false){
            if(it == null) return@getCoreThink
            TextShowActivity.launch(this,it.title,it.toStrShow(),delay)
        }
    }

    private fun startDataThink(){
        DataThinkActivity.launch(this)
    }
}

fun Activity.transparencyBar() {
    try {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }catch (_: Exception){}
}