package com.xb.selfrichapp.entity

import org.json.JSONArray
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
            dde.date = contentObj.optString("date")
            dde.bigEnv = parseBigEnv(contentObj.getJSONObject("qszs"))
            parseMainPlot(contentObj.optJSONArray("core_bk"),dde)
            dde.envDesc = contentObj.optString("pmlj")
            parseStockPlan(contentObj.optJSONArray("czjh"),dde)
            dde.stockRecord = parseStockRecord(contentObj.optJSONObject("cz_record"))
            dde.lastThink = contentObj.optString("zrfs")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dde
    }

    private fun parseBigEnv(json: JSONObject): BigEnvEntity{
        val be = BigEnvEntity()
        be.value = json.optString("value")
        be.desc = json.optString("desc")
        return be
    }

    private fun parseMainPlot(json: JSONArray?,dde:DayDataEntity){
        if(json == null) return
        val len = json.length()
        for (i in 0 until len) {
            val item = json.getJSONObject(i)
            val plot = MainPlot()
            plot.name = item.optString("name")
            plot.desc = item.optString("bk_desc")
            plot.core = item.optString("core_gg")
            dde.mainPlot.add(plot)
        }
    }

    private fun parseStockPlan(json: JSONArray?,dde:DayDataEntity){
        if(json == null) return
        val len = json.length()
        for (i in 0 until len) {
            val item = json.getJSONObject(i)
            val plan = StockPlan()
            plan.desc = item.optString("ly")
            plan.title = item.optString("desc")
            dde.nextDayPlan.add(plan)
        }
    }

    private fun parseStockRecord(json: JSONObject?): StockRecord?{
        if(json == null) return null
        val record = StockRecord()
        record.date = json.optString("date")
        val actionObj = json.optJSONArray("action") ?: return  null
        for (i in 0 until actionObj.length()) {
            val itemObj = actionObj.getJSONObject(i)
            val sa = StockAction()
            sa.name = itemObj.optString("name")
            sa.mode = itemObj.optInt("mode")
            sa.buy = itemObj.optDouble("buy")
            sa.sale = itemObj.optDouble("sale")
            record.action.add(sa)
        }
        return record
    }
}

class DayDataEntity {
    var date: String = ""
    var bigEnv: BigEnvEntity? = null
    val mainPlot = ArrayList<MainPlot>()
    var envDesc = ""
    val nextDayPlan = ArrayList<StockPlan>()
    var stockRecord : StockRecord? = null
    var lastThink = ""

    fun toShowText(): String{
        val sb = StringBuilder()
        sb.append(date).append("\n")
        sb.append("\n")
        bigEnv?.let {
            sb.append("指数系统：")
            sb.append(it.value).append("\n")
                .append(it.desc).append("\n")
        }
        sb.append("\n昨日盘面解析：")
        sb.append(envDesc).append("\n\n")
        sb.append("昨日反思：")
        sb.append(lastThink).append("\n\n")
        sb.append("当前主线：\n")
        for (plot in mainPlot) {
            sb.append(plot.name).append("\n")
            sb.append(plot.desc).append("\n")
            sb.append(plot.core).append("\n\n")
        }
        sb.append("交易计划：\n")
        for (plan in nextDayPlan) {
            sb.append(plan.title).append("\n")
            sb.append("理由：").append(plan.desc).append("\n\n")
        }
        return sb.toString()
    }
}

class BigEnvEntity{
    var value = ""
    var desc = ""
}

class MainPlot{
    var name = ""
    var desc = ""
    var core = ""
}

class StockPlan{
    var title = ""
    var desc = ""
}
//{
//    "date": "2026-05-08",
//    "action": [
//    { "name": "天华新能",
//        "mode": 0,
//        "buy": 11.23,
//        "sale": 13.21
//    }
//    ]
//}
class StockRecord{
    var date = ""
    val action = ArrayList<StockAction>()
}
class StockAction{
    var name = ""
    var mode = 0
    var buy = 0.0
    var sale = 0.0
}