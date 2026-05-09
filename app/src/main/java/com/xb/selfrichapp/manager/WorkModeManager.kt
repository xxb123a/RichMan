package com.xb.selfrichapp.manager

import android.util.Log
import com.xb.selfrichapp.db.StockRecordEntity
import com.xb.selfrichapp.db.fetchStockDao
import com.xb.selfrichapp.entity.CoreThinkEntity
import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.entity.EntityTools
import com.xb.selfrichapp.entity.StockRecord
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

    fun getCoreThink(isReload: Boolean = false,callback:(CoreThinkEntity?)-> Unit){
        val dis = Single.fromCallable {
            DataApi2.getCoreThink(isReload) }
            .map {
                val json = JSONObject(it)
                val cte = CoreThinkEntity()
                cte.title = json.optString("title")
                val coreArray = json.getJSONArray("core")
                for (i in 0 until coreArray.length()) {
                    cte.core.add(coreArray.optString(i))
                }
                cte
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback.invoke(it)
            },{
                callback.invoke(null)
            })
    }

    private fun syncStockRecord(data: StockRecord,force: Boolean){
        if(data.date.isEmpty()) return
        try {
            val timeSplitArray = data.date.split("-".toRegex())
            val time = Calendar.getInstance()
            time.set(timeSplitArray[0].toInt(),timeSplitArray[1].toInt() - 1,timeSplitArray[2].toInt())
            time.set(Calendar.HOUR_OF_DAY,0)
            time.set(Calendar.MINUTE,0)
            time.set(Calendar.SECOND,0)
            time.set(Calendar.MILLISECOND,0)
            val min = time.timeInMillis
            time.set(Calendar.HOUR_OF_DAY,23)
            time.set(Calendar.MINUTE,59)
            time.set(Calendar.SECOND,59)
            time.set(Calendar.MILLISECOND,999)
            val max = time.timeInMillis
            val hasRecord = if(force){
                fetchStockDao().deleteArea(min,max)
                false
            }else{
                fetchStockDao().findRecordByTimeArea(min,max).isNotEmpty()
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            Log.e("rich_man","添加记录-${sdf.format(min)} - ${sdf.format(max)} 是否有记录:$hasRecord")
            if(!hasRecord){
                val entityArray = ArrayList<StockRecordEntity>()
                for (action in data.action) {
                    val entity = StockRecordEntity()
                    entity.name = action.name
                    entity.mode = action.mode
                    entity.buy = action.buy
                    entity.modify_time = min
                    entity.sale = action.sale
                    entity.offset = (action.sale - action.buy) / action.buy
                    entityArray.add(entity)
                }
                fetchStockDao().add(entityArray)
            }
        }catch (_: Exception){}
    }

    fun getDayData(time: Long, isForce: Boolean, callback: (DayDataEntity) -> Unit) {
        val dis = Single.fromCallable {
             DataApi2.getDataByTime(isForce, time) }
            .map { EntityTools.parseDay(it) }
            .map {
                it.stockRecord?.let { record ->
                    syncStockRecord(record,isForce)
                }
                it
            }
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