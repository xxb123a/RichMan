package com.xb.selfrichapp.entity

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
 *_     (((__) (__)))
 * author      : xue
 * date        : 2026/5/9 14:56
 * description :
 */
class CoreThinkEntity {
    var title = ""
    val core = ArrayList<String>()

    fun toStrShow(): String {
        val sb = StringBuilder()
        for (string in core) {
            sb.append(string).append("\n\n")
        }
        return sb.toString()
    }
}