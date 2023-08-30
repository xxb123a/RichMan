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
import com.xb.selfrichapp.entity.EmotionalCycle
import com.xb.selfrichapp.manager.WorkModeManager
import com.xb.selfrichapp.prefs.Prefs

class MainActivity : AppCompatActivity() {

    private var mLastLoadTime = 0L
    private val mMainView by lazy { findViewById<View>(R.id.main_content) }
    private val mTvContent by lazy { findViewById<TextView>(R.id.tv_content) }
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
        if (dde.limitDown.isEmpty()) {
            mTvContent.text = "暂时无数据"
            mMainView.setBackgroundColor(Color.WHITE)
            mTvContent.setTextColor(Color.BLACK)
            return
        }
        val da = dde.mNextDayAction
        val sb = StringBuilder()
        val lastBoomValue = dde.mBoom.lastBoomValue()
        sb.append("昨日炸板今日情绪值 : ").append(lastBoomValue)
        if (lastBoomValue < 0) {
            sb.append(" 明日注意风险")
        }
        val boomValue = dde.mBoom.boomFearCount()
        sb.append("\n今日断板大面个数 : ").append(boomValue)
        if (boomValue > 0) {
            sb.append(" 断板有大面，注意风险")
        }

        val mEmotionalCycle = dde.mEmotionalCycle
        val lastEc = WorkModeManager.findPreDayData(mLastLoadTime)?.mEmotionalCycle
        if(lastEc != null){
            sb.append("\n昨日势能：").append(lastEc.getPotentialEnergy())
            sb.append("昨日动能：").append(lastEc.getKineticEnergy())
        }
        sb.append("\n势能：").append(mEmotionalCycle.getPotentialEnergy())

        val ke = mEmotionalCycle.getKineticEnergy()
        sb.append("动能：").append(ke)

        if (ke > 0) {
            mMainView.setBackgroundColor(Color.GREEN)
            mTvContent.setTextColor(Color.WHITE)
        } else {
            mMainView.setBackgroundColor(Color.RED)
            mTvContent.setTextColor(Color.WHITE)
        }

        sb.append("\n系统推演：").append(mEmotionalCycle.getAutoInfer())
        if (da.zqId in 4..5) {
            sb.append("\n 警告 : \n 今日很危险，危险，危险").append("\n")
        }
        sb.append("总结:\n").append(da.summarize)

        val pd = WorkModeManager.findPeriodicNode(da.zqId)
        sb.append("\n推演周期: ").append(pd.name).append("\n")

        if (da.strategyArray.isEmpty()) {
            sb.append("没有操作 休息 不要头脑发热去亏钱 很危险")
        } else {
            for (strategyEntity in da.strategyArray) {
                val mode = WorkModeManager.findBattleMode(strategyEntity.battleMode)
                sb.append("模式:").append(mode.name).append("\n")
                sb.append("满足条件:").append(strategyEntity.cause).append("\n")
                sb.append("目标:").append(strategyEntity.target).append("\n")
                sb.append("其他:").append(strategyEntity.info).append("\n")
            }
        }
        mTvContent.text = sb.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, R.id.main_setting, 0, "设置")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_setting) {
            CommonSettingActivity.launch(this)
        }
        return super.onOptionsItemSelected(item)
    }
}