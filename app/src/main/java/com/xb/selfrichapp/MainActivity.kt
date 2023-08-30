package com.xb.selfrichapp

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.act.CommonSettingActivity
import com.xb.selfrichapp.act.TextShowActivity
import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.manager.WorkModeManager
import com.xb.selfrichapp.prefs.Prefs

class MainActivity : AppCompatActivity() {

    private var mLastLoadTime = 0L
    private val mMainView by lazy { findViewById<View>(R.id.main_content) }
    private val mTvContent by lazy { findViewById<TextView>(R.id.tv_content) }
    private var mDayData:DayDataEntity? = null
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
            mDayData = null
            mTvContent.text = "暂时无数据"
            mMainView.setBackgroundColor(Color.WHITE)
            mTvContent.setTextColor(Color.BLACK)
            return
        }
        var calcQxz = 0
        mDayData = dde
        val da = dde.mNextDayAction
        val sb = StringBuilder()
        val lastBoomValue = dde.mBoom.lastBoomValue()
        sb.append("昨日炸板今日情绪值 : ").append(lastBoomValue)
        calcQxz += if(lastBoomValue == 0){
            5
        }else if(lastBoomValue > 0){
            10
        }else{
            0
        }
        if (lastBoomValue < 0) {
            sb.append(" 明日注意风险")
            //10
        }
        sb.append("\n")
        val boomValue = dde.mBoom.boomFearCount()
        sb.append("\n今日断板大面个数 : ").append(boomValue)
        if (boomValue > 0) {
            sb.append(" 断板有大面，注意风险")
            //20
        }

        calcQxz += when (boomValue) {
            0 -> {
                20
            }
            1 -> {
                10
            }
            else -> {
                0
            }
        }

        sb.append("\n")
        val mEmotionalCycle = dde.mEmotionalCycle
        val lastEc = WorkModeManager.findPreDayData(mLastLoadTime)?.mEmotionalCycle
        if(lastEc != null){
            sb.append("\n昨日势能：").append(lastEc.getPotentialEnergy())
            sb.append("昨日动能：").append(lastEc.getKineticEnergy())
        }
        val ke = mEmotionalCycle.getKineticEnergy()
        val pe = mEmotionalCycle.getPotentialEnergy()
        sb.append("\n势能：").append(pe)
        sb.append(" 动能：").append(ke)
        sb.append("\n")
        if (ke > 0) {
            mMainView.setBackgroundColor(Color.GREEN)
            mTvContent.setTextColor(Color.WHITE)
        } else {
            mMainView.setBackgroundColor(Color.RED)
            mTvContent.setTextColor(Color.WHITE)
        }
        //50
        calcQxz += calcQxByPe(pe)
        calcQxz += calcQxByKe(ke)


        sb.append("\n系统推演：").append(mEmotionalCycle.getAutoInfer())
        sb.append("\n")
        if (da.zqId in 4..5) {
            sb.append("\n警告 : \n 今日很危险，危险，危险").append("\n")
        }
        sb.append("\n总结:\n").append(da.summarize)

        val pd = WorkModeManager.findPeriodicNode(da.zqId)
        sb.append("\n\n推演周期: ").append(pd.name).append("\n\n")
        //20
        calcQxz += when(pd.id){
            in 1..2->20
            in 3..5->10
            else -> 0
        }
        if (da.strategyArray.isEmpty()) {
            sb.append("没有操作 休息 不要头脑发热去亏钱 很危险")
        } else {
            for (strategyEntity in da.strategyArray) {
                val mode = WorkModeManager.findBattleMode(strategyEntity.battleMode)
                sb.append("模式:").append(mode.name).append("\n\n")
                sb.append("满足条件:").append(strategyEntity.cause).append("\n\n")
                sb.append("目标:").append(strategyEntity.target).append("\n\n")
                sb.append("其他:\n").append(strategyEntity.info).append("\n\n")
            }
        }
        sb.append("\n今日情绪分：").append(calcQxz)
        mTvContent.text = sb.toString()
    }


    private fun calcQxByPe(pe:Int):Int{
        //10
       return when (pe) {
           -10 -> {
               0
           }
           -6 -> {
               2
           }
           -2 -> {
               4
           }
           2 -> {
               6
           }
           6 -> {
               8
           }
           10 -> {
               10
           }
           else -> {
               0
           }
       }
    }
    private fun calcQxByKe(ke:Int):Int{
        //40 分
        return when (ke) {
            -12 -> {
                0
            }
            -8 -> {
                5
            }
            -4 -> {
                10
            }
            0 -> {
                20
            }
            4 -> {
                30
            }
            8 -> {
                35
            }
            12 -> {
                40
            }
            else -> {
                0
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, R.id.main_setting, 0, "设置")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        menu?.add(0, R.id.main_limit_down, 0, "连板数据")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        menu?.add(0, R.id.main_user_action, 0, "用户操作")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_setting -> {
                CommonSettingActivity.launch(this)
            }
            R.id.main_limit_down -> {
                mDayData?.let {
                    TextShowActivity.launch(this,"连板数据",it.createLimitDownShowText())
                }
            }
            R.id.main_user_action -> {
                mDayData?.let {
                    TextShowActivity.launch(this,"用户操作",it.createUserActionShowText())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}