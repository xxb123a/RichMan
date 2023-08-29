package com.xb.selfrichapp.act

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.R
import com.xb.selfrichapp.http.DataApi
import com.xb.selfrichapp.manager.WorkModeManager
import com.xb.selfrichapp.prefs.Prefs
import com.xb.selfrichapp.tool.ToastTools
import java.text.SimpleDateFormat
import java.util.*

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
 * date        : 2023/8/29 16:24
 * description :
 */
class DaySettingActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, DaySettingActivity::class.java))
        }
    }

    private val mTvShowTime by lazy { findViewById<TextView>(R.id.tv_show_time) }
    private val mTvDayStatus by lazy { findViewById<TextView>(R.id.tv_day_status) }
    private val mBtnDay by lazy { findViewById<TextView>(R.id.btn_day_get) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_set)
        commonClick(R.id.btn_modify_show_time) {
            showDatePicker{
                Prefs.mShowDataTime = it
                refreshShowTime()
            }
        }
        commonClick(R.id.btn_day_get) {
            val time = Prefs.mShowDataTime
            if(time == 0L){
                ToastTools.showText("请先设置展示时间")
                return@commonClick
            }
            requestDayData(time)
        }
        refreshShowTime()
    }

    private fun requestDayData(time:Long){
        WorkModeManager.getDayData(time,true){
            refreshDayUI(time)
            ToastTools.showText("加载成功了哟")
        }
    }

    private fun showDatePicker(callback:(Long)->Unit){
        DatePickerDialog(this).apply {
            setOnDateSetListener{
                view,year,month,day->
                val calendar = Calendar.getInstance()
                calendar.set(year,month,day)
                callback.invoke(calendar.timeInMillis)
            }
            show()
        }
    }

    private fun refreshShowTime() {
        val time = Prefs.mShowDataTime
        val text = if (time > 0) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            "首页展示时间: ${sdf.format(time)}"
        } else {
            "首页展示时间:"
        }
        mTvShowTime.text = text

        refreshDayUI(time)
    }

    @SuppressLint("SetTextI18n")
    private fun refreshDayUI(time:Long){
        //先判断是否有数据
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if(DataApi.hasDownloadDayContent(time)){
            mTvDayStatus.text = "${sdf.format(time)} 已有数据"
            mBtnDay.text = "更新"
        }else{
            mTvDayStatus.text = "${sdf.format(time)} 暂无数据"
            mBtnDay.text = "获取"
        }
    }

    private fun commonClick(@IdRes id: Int, callback: () -> Unit) {
        findViewById<View>(id).setOnClickListener { callback.invoke() }
    }
}