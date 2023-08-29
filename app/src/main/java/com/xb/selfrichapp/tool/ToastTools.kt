package com.xb.selfrichapp.tool

import android.widget.Toast
import com.xb.selfrichapp.TheApp

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
 * date        : 2023/8/29 16:55
 * description :
 */
object ToastTools {
    fun showText(text:String){
        Toast.makeText(TheApp.mInstance,text, Toast.LENGTH_LONG).show()
    }
}