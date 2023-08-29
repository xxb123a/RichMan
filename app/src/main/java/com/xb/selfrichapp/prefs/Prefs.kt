package com.xb.selfrichapp.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

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
 * date        : 2023/8/29 16:22
 * description :
 */

object Prefs {
    var mShowDataTime by PrefsDelegate("prefs_main_show_time",0L)
}

class PreferenceManager(ctx: Context, val name: String) : IPreferenceManager {
    private val mSharedPreferences by lazy { ctx.getSharedPreferences(name, Context.MODE_PRIVATE) }

    companion object {
        private const val SETTINGS_FILENAME = "com_tb_player.prefs"
        private const val CACHES_FILENAME = "com_tb_player_cache.prefs"
        private var mInstance: PreferenceManager? = null
        private var mCache: PreferenceManager? = null
        fun initSingleton(ctx: Context) {
            mInstance = PreferenceManager(ctx, SETTINGS_FILENAME)
            mCache = PreferenceManager(ctx, CACHES_FILENAME)
        }

        fun getInstance() = mInstance
        fun getCache() = mCache
    }

    override fun putInt(key: String, value: Int) = put {
        putInt(key, value)
    }

    override fun getInt(key: String, defaultValue: Int) = getValue {
        getInt(key, defaultValue)
    }

    override fun putString(key: String, value: String) = put {
        putString(key, value)
    }

    override fun getString(key: String, defaultValue: String) = getValue {
        getString(key, defaultValue)
    }


    override fun getStringSet(key: String, defaultValue: Set<String>) = getValue {
        getStringSet(key, defaultValue)
    }

    override fun putStringSet(key: String, value: Set<String>) = put {
        putStringSet(key, value)
    }

    override fun putLong(key: String, value: Long) = put {
        putLong(key, value)
    }

    override fun getLong(key: String, defaultValue: Long) = getValue {
        getLong(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) = put {
        putBoolean(key, value)
    }

    override fun getBoolean(key: String, defaultValue: Boolean) = getValue {
        getBoolean(key, defaultValue)
    }

    private fun <T> getValue(block: SharedPreferences.() -> T): T {
        return block(mSharedPreferences)
    }

    private fun put(isApply: Boolean = true, block: SharedPreferences.Editor.() -> Unit) {
        val editor = mSharedPreferences.edit()
        block(editor)
        if (isApply) editor.apply() else editor.commit()
    }

}


private class PrefsDelegate<T>(val key: String, val defaultValue: T) {
    private var isCaches = false
    fun markCache(): PrefsDelegate<T> {
        isCaches = true
        return this
    }

    fun providerPrefsManager(): IPreferenceManager? {
        return if (isCaches) PreferenceManager.getCache() else PreferenceManager.getInstance()
    }

    private fun <T> getPrefsValue(block: IPreferenceManager.() -> T): T {
        val obj = providerPrefsManager()
        return if (obj != null) {
            block(obj)
        } else {
            defaultValue as T
        }
    }

    private fun setPrefsValue(block: IPreferenceManager.() -> Unit) {
        providerPrefsManager()?.let {
            block(it)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPrefsValue {
            when (defaultValue) {
                is String -> getString(key, defaultValue)
                is Boolean -> getBoolean(key, defaultValue)
                is Long -> getLong(key, defaultValue)
                is Int -> getInt(key, defaultValue)
                else -> defaultValue
            } as T
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setPrefsValue {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
            }
        }
    }
}


interface IPreferenceManager {
    fun putInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int): Int

    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String): String?

    fun getStringSet(key: String, defaultValue: Set<String>): Set<String>?

    fun putStringSet(key: String, value: Set<String>)

    fun putLong(key: String, value: Long)

    fun getLong(key: String, defaultValue: Long): Long

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean
}