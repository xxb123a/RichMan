package com.xb.selfrichapp.entity

import com.xb.selfrichapp.manager.WorkModeManager

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
 * date        : 2023/8/28 17:47
 * description : 周期节点
 */
class PeriodicNode(val id: Int, val name: String, val desc: String)

class BattleMode(
    val name: String,
    val id: Int,
    val suitPeriodicNode: List<Int>,
    val case: List<String>
){
    fun getShowTitle():String{
        val sb = StringBuilder()
        sb.append(name)
        if(suitPeriodicNode.isEmpty())return sb.toString()
        sb.append("\n适用周期：\n")
        for (i in suitPeriodicNode) {
            val pd = WorkModeManager.findPeriodicNode(i)
            sb.append(pd.name).append(":").append(pd.desc).append("\n")
        }
        return sb.toString()
    }
    fun getShowContent():String{
        val sb = StringBuilder()
        for (index in case.indices) {
            sb.append(index+1).append(".")
            sb.append(case[index])
            sb.append("\n")
        }
        return sb.toString()
    }
}