package com.xb.selfrichapp.manager

import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.entity.EntityTools
import com.xb.selfrichapp.http.DataApi2
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
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
 * date        : 2023/8/28 17:46
 * description :
 */
object WorkModeManager {
    private val mHolidays = ArrayList<String>()

    fun getDayData(time: Long, isForce: Boolean, callback: (DayDataEntity) -> Unit) {
        val dis = Single.fromCallable {
             DataApi2.getDataByTime(isForce, time) }
            .map { EntityTools.parseDay(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callback.invoke(it) }
    }

    private fun getLocalDayData(time:Long):DayDataEntity?{
        val content = DataApi2.getLocalDayData(time)
        if(content.isEmpty())return null
        return EntityTools.parseDay(content)
    }

    private fun parseHoliday(content: String) {
        try {
            val data = JSONObject(content).getJSONArray("data")
            val list = ArrayList<String>()
            for (idx in 0 until data.length()) {
                list.add(data.optString(idx))
            }
            mHolidays.clear()
            mHolidays.addAll(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findLastActionDay(time:Long):Long{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        while (true){
            calendar.add(Calendar.DATE,-1)
            if(!isHoliday(calendar.timeInMillis)){
                return calendar.timeInMillis
            }
        }
    }

    fun isHoliday(time: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val sdf = SimpleDateFormat("MMdd", Locale.getDefault())
        val date = sdf.format(calendar.time)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            return true
        }
        for (mHoliday in mHolidays) {
            if (date == mHoliday) {
                return true
            }
        }
        return false
    }

    fun findPreDayData(time:Long):DayDataEntity?{
        val lastTime = findLastActionDay(time)
        return getLocalDayData(lastTime)
    }

}