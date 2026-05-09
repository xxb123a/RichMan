package com.xb.selfrichapp.act

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.xb.selfrichapp.R
import com.xb.selfrichapp.db.StockRecordEntity
import com.xb.selfrichapp.db.fetchStockDao
import com.xb.selfrichapp.transparencyBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
class DataThinkActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, DataThinkActivity::class.java))
        }
    }

    private var startTime = 0L
    private var endTime = 0L
    private val startTimeUI by lazy { findViewById<TextView>(R.id.time_start) }
    private val endTimeUI by lazy { findViewById<TextView>(R.id.time_end) }
    private val contentUI by lazy { findViewById<TextView>(R.id.txt_content) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparencyBar()
        setContentView(R.layout.activity_data_think)
        val calendar = Calendar.getInstance()
        endTime = calendar.timeInMillis
        calendar.add(Calendar.MONTH,-1)
        startTime = calendar.timeInMillis
        commonClick(R.id.time_start){
            showDatePicker{
                startTime = it
                refreshTimeUI()
            }
        }

        commonClick(R.id.time_end){
            showDatePicker {
                endTime = it
                refreshTimeUI()
            }

        }
        commonClick(R.id.start_think){
            startThink()
        }
        refreshTimeUI()
        startThink()
    }

    private fun refreshTimeUI(){
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        startTimeUI.text = sdf.format(startTime)
        endTimeUI.text = sdf.format(endTime)
    }

    private fun startThink(){
        contentUI.text = "思考中，请稍等"
        Thread{
            val sb = StringBuilder()
            sb.append("模式内：\n\n")
            createStr(sb,
                fetchStockDao().findRecordWinMode(startTime,endTime),
                fetchStockDao().findRecordLoseMode(startTime,endTime)
            )
            sb.append("总共：\n\n")
            createStr(sb,
                fetchStockDao().findRecordWin(startTime,endTime),
                fetchStockDao().findRecordLose(startTime,endTime)
            )

            contentUI.post {
                contentUI.text = sb.toString()
            }
        }.start()
    }

    private fun createStr(sb: StringBuilder,win:List<StockRecordEntity>,lose:List<StockRecordEntity>){
        val winSize = win.size
        val loseSize = lose.size
        if(winSize == 0 && loseSize == 0) return
        sb.append("共操作").append(winSize + loseSize).append("笔\n\n")
        sb.append("盈利：").append(winSize).append("    亏损:").append(loseSize).append("\n\n")
        val winRate = (winSize * 1000 / (winSize + loseSize)) / 10.0
        sb.append("胜率：").append(winRate).append("%\n\n")
        val winAvg = avgOffset(win)
        val loseAvg = avgOffset(lose)
        val maxWin = win.maxByOrNull { it.offset }
        val maxLose = lose.minByOrNull { it.offset }
        sb.append("平均盈利：").append((winAvg * 1000).toInt()/10.0).append("%\n\n")
        sb.append("平均亏损：").append((loseAvg * 1000).toInt()/10.0).append("%\n\n")
        if(maxWin != null){
            sb.append("最大盈利：")
                .append(maxWin.name).append(" ")
                .append((maxWin.offset * 1000).toInt()/10.0).append("%\n\n")
        }
        if(maxLose != null){
            sb.append("最大亏损：")
                .append(maxLose.name).append(" ")
                .append((maxLose.offset * 1000).toInt()/10.0).append("%\n\n")
        }
    }

    private fun avgOffset(data:List<StockRecordEntity>): Double{
        if(data.isEmpty()) return 0.0
        return data.sumOf { it.offset } / data.size
    }

    private fun commonClick(@IdRes id: Int, callback: () -> Unit) {
        findViewById<View>(id).setOnClickListener { callback.invoke() }
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
}