package com.xb.selfrichapp.http

import android.annotation.SuppressLint
import android.icu.util.Calendar
import com.xb.selfrichapp.TheApp
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.net.Proxy
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

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
 * date        : 2023/8/28 17:21
 * description :
 */
object DataApi {
    //base host
    const val base_host = "https://raw.githubusercontent.com/xxb123a/RichMan/main"
    const val zfJson = "/zf.json"

    private fun getHolidayName(year:Int):String{
        return "$base_host/Holidays/$year.json"
    }

    private fun createDataName(time: Long): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MMdd", Locale.getDefault())
        return "/${calendar.get(Calendar.YEAR)}/${sdf.format(time)}.json"
    }

    fun getZfContent(isReload: Boolean = false): String {
        if (!isReload) {
            val cacheContent = readStringByFile(getZfCachePath())
            if (cacheContent.isNotEmpty()) {
                return cacheContent
            }
        }
        val content = getUrlContent(base_host + zfJson, createCommonOkHttp())
        if (content.isNotEmpty() && content.trim().startsWith("{")) {
            save2File(content, getZfCachePath())
        }
        return content
    }

    fun getDataByTime(isReload:Boolean, time: Long): String {
        if(!isReload){
            val cacheContent = readStringByFile(getDataPath(time))
            if (cacheContent.isNotEmpty()) {
                return cacheContent
            }
        }
        val content = getUrlContent(base_host + createDataName(time), createCommonOkHttp())
        if (content.isNotEmpty() && content.trim().startsWith("{")) {
            save2File(content, getDataPath(time))
        }
        return content
    }

    fun hasDownloadDayContent(time:Long):Boolean{
        val cacheContent = readStringByFile(getDataPath(time))
        return cacheContent.isNotEmpty()
    }

    fun getHolidayContent(year:Int):String{
        val cacheContent = readStringByFile(getHolidayPath(year))
        if (cacheContent.isNotEmpty()) {
            return cacheContent
        }
        val content = getUrlContent(getHolidayName(year), createCommonOkHttp())
        if (content.isNotEmpty() && content.trim().startsWith("{")) {
            save2File(content, getHolidayPath(year))
        }
        return content
    }

    private fun getUrlContent(url: String, okClient: OkHttpClient): String {
        try {
            val request = Request.Builder().url(url).build()
            val response = okClient.newCall(request).execute()
            if (response.isSuccessful) {
                return response.body()?.string() ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    private fun getCacheRoot(): String {
        val file = File(TheApp.mInstance.cacheDir, "rich")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath
    }

    private fun getDataCachePath(): String {
        val file = File(getCacheRoot(), "data_list")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath
    }

    private fun getHolidayCachePath():String{
        val file = File(getCacheRoot(), "holiday")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath
    }

    private fun getOtherCachePath(): String {
        val file = File(getCacheRoot(), "other")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath
    }

    private fun getDataPath(time: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return File(getDataCachePath(), sdf.format(time) + ".json").absolutePath
    }

    private fun getZfCachePath(): String {
        return File(getOtherCachePath(), "zf.json").absolutePath
    }

    private fun getHolidayPath(year:Int):String{
        return File(getHolidayCachePath(), "$year.json").absolutePath
    }

    private fun readStringByFile(path: String): String {
        val file = File(path)
        if (file.exists() && file.length() > 0) {
            try {
                val ins = FileInputStream(file)
                val result = String(ins.readBytes())
                ins.close()
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun save2File(content: String, path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
            val os = BufferedWriter(FileWriter(file))
            os.write(content)
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCommonOkHttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(150000, TimeUnit.MILLISECONDS)
            .readTimeout(150000, TimeUnit.MILLISECONDS)
            .hostnameVerifier(TrustAllHostnameVerifier())
            .proxy(Proxy.NO_PROXY)
        val sf = createSSLSocketFactory()
        if (sf != null) {
            builder.sslSocketFactory(sf)
        }
        return builder.build()
    }

    @SuppressLint("CustomX509TrustManager")
    private class TrustAllCerts : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    private class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        try {
            val ctx = SSLContext.getInstance("TLS")
            ctx.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
            return ctx.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}