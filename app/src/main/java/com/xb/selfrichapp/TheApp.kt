package com.xb.selfrichapp

import android.app.Application
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
 * date        : 2023/8/28 17:27
 * description :
 */
class TheApp : Application() {
    companion object {
        lateinit var mInstance: TheApp
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        WorkModeManager.init()
    }
}