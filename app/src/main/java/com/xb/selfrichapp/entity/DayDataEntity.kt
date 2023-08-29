package com.xb.selfrichapp.entity

import org.json.JSONObject

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
 * date        : 2023/8/29 10:44
 * description :
 */

object EntityTools {
    fun parseDay(content: String): DayDataEntity {
        val dde = DayDataEntity()
        try {
            val contentObj = JSONObject(content)
            dde.previousDate = contentObj.optString("previous_date")
            dde.limitDown = parseLimitDown(contentObj.getJSONObject("limit_down"))
            dde.userAction = parseUserAction(contentObj.getJSONObject("user_action"))
            dde.mEmotionalCycle = parseEmotionalCycle(contentObj.getJSONObject("emotional_cycle"))
            dde.mNextDayAction = parseNextAction(contentObj.getJSONObject("next_action"))
            dde.mBoom = parseBoom(contentObj.getJSONObject("boom"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dde
    }

    private fun parseLimitDown(json: JSONObject): List<StockEntity> {
        val result = ArrayList<StockEntity>()
        try {
            val jarray = json.getJSONArray("array")
            for (idx in 0 until jarray.length()) {
                val itemObj = jarray.getJSONObject(idx)
                val se = StockEntity()
                se.name = itemObj.optString("name")
                se.lastPrice = itemObj.optDouble("lastDayPrice").toFloat()
                se.endPrice = itemObj.optDouble("endPrice").toFloat()
                se.maxPrice = itemObj.optDouble("max").toFloat()
                se.minPrice = itemObj.optDouble("min").toFloat()
                se.startPrice = itemObj.optDouble("start").toFloat()
                se.level = itemObj.optInt("level")
                se.desc = itemObj.optString("desc")
                result.add(se)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun parseUserAction(json: JSONObject): List<StockAction> {
        val result = ArrayList<StockAction>()
        try {
            val actionArray = json.getJSONArray("action")
            for (idx in 0 until actionArray.length()) {
                val itemObj = actionArray.getJSONObject(idx)
                val sa = StockAction()
                sa._do = itemObj.optInt("do")
                sa.name = itemObj.optString("name")
                sa.mode = itemObj.optInt("mode")
                sa.isModeI = itemObj.optBoolean("isModeI")
                sa.buyPrice = itemObj.optDouble("buyPrice").toFloat()
                sa.sellPrice = itemObj.optDouble("sellPrice").toFloat()
                sa.value = itemObj.optInt("value")
                result.add(sa)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun parseEmotionalCycle(json: JSONObject): EmotionalCycle {
        val ec = EmotionalCycle()
        ec.lbs = json.optInt("lbs")
        ec.sbs = json.optInt("sbs")
        ec.minFive = json.optInt("min_five")
        ec.hpb = json.optDouble("hpb").toFloat()
        ec.kq = json.optDouble("kq").toFloat()
        ec.shpb = json.optDouble("shpb").toFloat()
        ec.sbdmb = json.optDouble("sbdmb").toFloat()
        ec.lhpb = json.optDouble("lhpb").toFloat()
        ec.lbdmb = json.optDouble("lbdmb").toFloat()
        ec.lbbl = json.optDouble("lbbl").toFloat()
        ec.lbWlb = json.optDouble("lb_wlb").toFloat()
        ec.fbv = json.optDouble("fbv").toFloat()
        ec._20hpb = json.optDouble("_20hpb").toFloat()
        ec._20sfbl = json.optDouble("_20sfbl").toFloat()
        ec.zq = json.optInt("zq")
        val hotArray = json.getJSONArray("hot_block")
        for (idx in 0 until hotArray.length()) {
            ec.hotBlock.add(hotArray.optString(idx))
        }
        return ec
    }

    private fun parseNextAction(json: JSONObject): DayStrategy {
        val ds = DayStrategy()
        ds.summarize = json.optString("summarize")
        ds.zqId = json.optInt("zq")
        val sArray = json.getJSONArray("strategy")
        for (idx in 0 until sArray.length()) {
            val itemObj = sArray.getJSONObject(idx)
            val se = StrategyEntity()
            se.battleMode = itemObj.optInt("battleMode")
            se.cause = itemObj.optString("cause")
            se.target = itemObj.optString("target")
            se.info = itemObj.optString("info")
            ds.strategyArray.add(se)
        }
        return ds
    }

    private fun parseBoom(json: JSONObject): BoomEntity {
        val be = BoomEntity()
        val dataArray = json.getJSONArray("data")
        for (idx in 0 until dataArray.length()) {
            val itemJson = dataArray.getJSONObject(idx)
            val boomStock = BoomStock()
            boomStock.name = itemJson.optString("name")
            boomStock.lastDayPrice = itemJson.optDouble("lastDayPrice").toFloat()
            boomStock.endPrice = itemJson.optDouble("endPrice").toFloat()
            boomStock.max = itemJson.optDouble("max").toFloat()
            boomStock.min = itemJson.optDouble("min").toFloat()
            boomStock.startPrice = itemJson.optDouble("start").toFloat()
            boomStock.level = itemJson.optInt("level")
            boomStock.desc = itemJson.optString("desc")
            be.data.add(boomStock)
        }
        val br = BoomLastResult()
        val lastObj = json.getJSONObject("last_result")
        br.desc = lastObj.optString("desc")
        br.hpb = lastObj.optDouble("hpb").toFloat()
        br.dmb = lastObj.optDouble("dmb").toFloat()
        be.lastResult = br
        return be
    }
}

class DayDataEntity {
    var previousDate = ""
    var limitDown: List<StockEntity> = arrayListOf()
    var userAction: List<StockAction> = arrayListOf()
    var mEmotionalCycle: EmotionalCycle = EmotionalCycle()
    var mNextDayAction = DayStrategy()
    var mBoom = BoomEntity()
}

//"name": "正和生态",
//"lastDayPrice": 13.81,
//"endPrice": 15.19,
//"max": 15.19,
//"min": 12.43,
//"start": 12.8,
//"level": 5,
//"desc": "环保-日本福岛排污"
class StockEntity {
    var name = ""
    var lastPrice = 0f
    var startPrice = 0f
    var endPrice = 0f
    var maxPrice = 0f
    var minPrice = 0f
    var level = 1
    var desc = ""

}

//{
//    "do": 0,
//    "name": "久其",
//    "mode": 1,
//    "isModeI": false,
//    "buyPrice": 15.14,
//    "sellPrice": 13.14,
//    "value": 100
//}
class StockAction {
    var _do = 0 //1 买入 0 卖出
    var name = ""
    var mode = 0
    var isModeI = false
    var buyPrice = 0f
    var sellPrice = 0f
    var value = 100 //买入数量
}

class EmotionalCycle {
    //连板
    var lbs = 0
    var sbs = 0

    //小于 -5
    var minFive = 0

    //红盘比
    var hpb = 0f

    //亏钱效应
    var kq = 0f

    //首板红攀比
    var shpb = 0f

    //首板大面壁
    var sbdmb = 0f

    //连板红攀比
    var lhpb = 0f

    //连板大面壁
    var lbdmb = 0f

    //连板比例
    var lbbl = 0f

    //连板未连板绿攀比
    var lbWlb = 0f

    //封板率
    var fbv = 0f

    //20 红攀比
    var _20hpb = 0f

    //20 首板封板率
    var _20sfbl = 0f

    //周期
    var zq = 0

    // 热点板块
    var hotBlock = arrayListOf<String>()
}

class DayStrategy {
    var summarize = ""
    var zqId = 0
    var strategyArray = arrayListOf<StrategyEntity>()
}

//"battleMode": 0,
//"cause":"",
//"target":"",
//"info":""
class StrategyEntity {
    var battleMode = 0
    var cause = ""
    var target = ""
    var info = ""
}

class BoomEntity {
    var data = arrayListOf<BoomStock>()
    var lastResult = BoomLastResult()
}

//"name": "正和生态",
//"lastDayPrice": 13.81,
//"endPrice": 15.19,
//"max": 15.19,
//"min": 12.43,
//"start": 12.8,
//"level": 5,
//"desc": "环保-日本福岛排污"
class BoomStock {
    var name = ""
    var lastDayPrice = 0f
    var endPrice = 0f
    var max = 0f
    var min = 0f
    var startPrice = 0f
    var level = 0
    var desc = ""
}

class BoomLastResult {
    var desc = ""
    var hpb = 0f
    var dmb = 0f
}