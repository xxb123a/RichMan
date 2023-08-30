package com.xb.selfrichapp.manager

import com.xb.selfrichapp.entity.BattleMode
import com.xb.selfrichapp.entity.DayDataEntity
import com.xb.selfrichapp.entity.EntityTools
import com.xb.selfrichapp.entity.PeriodicNode
import com.xb.selfrichapp.http.DataApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
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
 * date        : 2023/8/28 17:46
 * description :
 */
object WorkModeManager {
    private val mHolidays = ArrayList<String>()
    private val mPeriodicNodeList = Collections.synchronizedList(ArrayList<PeriodicNode>())
    private val mBattleModeList = Collections.synchronizedList(ArrayList<BattleMode>())
    private var isInitLoad = false

    fun init() {
        refreshZf(false) {}
        val year = Calendar.getInstance().get(Calendar.YEAR)
        refreshHoliday(year) {

        }
    }


    fun refreshZf(isForce: Boolean, callback: () -> Unit) {
        val dis = Observable.just(isForce)
            .map { DataApi.getZfContent(it) }
            .map { parse2ZfInfo(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callback.invoke() }
    }

    fun refreshHoliday(year: Int, callback: () -> Unit) {
        val dis = Observable.just(year)
            .map { DataApi.getHolidayContent(it) }
            .map { parseHoliday(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callback.invoke() }
    }

    fun getDayData(time: Long, isForce: Boolean, callback: (DayDataEntity) -> Unit) {
        val dis = Observable.just(time)
            .map { DataApi.getDataByTime(isForce, it) }
            .map { EntityTools.parseDay(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callback.invoke(it) }
    }

    private fun getLocalDayData(time:Long):DayDataEntity?{
        val content = DataApi.getLocalDayData(time)
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

    private fun parse2ZfInfo(content: String): Boolean {
        try {
            mBattleModeList.clear()
            mPeriodicNodeList.clear()
            val json = JSONObject(content)
            val qxzq = json.getJSONArray("qxzq")
            for (idx in 0 until qxzq.length()) {
                val itemObj = qxzq.getJSONObject(idx)
                mPeriodicNodeList.add(
                    PeriodicNode(
                        itemObj.optInt("id"),
                        itemObj.optString("name"),
                        itemObj.optString("desc")
                    )
                )
            }

            val battleMode = json.getJSONArray("fun")
            for (idx in 0 until battleMode.length()) {
                val itemObj = battleMode.getJSONObject(idx)
                val zqArray = itemObj.getJSONArray("zq")
                val caseArray = itemObj.getJSONArray("case")
                val zqList = ArrayList<Int>()
                for (zi in 0 until zqArray.length()) {
                    zqList.add(zqArray.optInt(zi))
                }
                val caseList = ArrayList<String>()

                for (ci in 0 until caseArray.length()) {
                    caseList.add(caseArray.optString(ci))
                }
                mBattleModeList.add(
                    BattleMode(
                        itemObj.optString("name"),
                        itemObj.optInt("id"),
                        zqList,
                        caseList
                    )
                )
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
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

    fun getPeriodicNode(): List<PeriodicNode> {
        return mPeriodicNodeList
    }

    fun getBattleMode(): List<BattleMode> {
        return mBattleModeList
    }

    fun findPreDayData(time:Long):DayDataEntity?{
        val lastTime = findLastActionDay(time)
        return getLocalDayData(lastTime)
    }

    fun findPeriodicNode(id: Int): PeriodicNode {
        for (periodicNode in mPeriodicNodeList) {
            if (periodicNode.id == id) return periodicNode
        }
        return PeriodicNode(-1, "通用", "适用所有周期 只需要满足条件")
    }

    fun findBattleMode(id: Int): BattleMode {
        for (battleMode in mBattleModeList) {
            if (battleMode.id == id) {
                return battleMode
            }
        }
        return BattleMode("未知", -1, arrayListOf(), arrayListOf())
    }
}