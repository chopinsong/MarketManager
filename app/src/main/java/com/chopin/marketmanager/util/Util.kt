package com.chopin.marketmanager.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.chopin.marketmanager.ui.PSActivity
import java.io.File
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


object Util {
    fun toWeak(context: Context): WeakReference<Context> {
        return WeakReference(context)
    }

    fun time(): String {
        val fm = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }

    fun time2long(s: String): Long {
        val fm = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        return fm.parse(s).time
    }

    fun showPSActivity(context: Context, isP: Boolean) {
        val i = Intent(context, PSActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("isP", isP)
        context.startActivity(i)
    }

    fun obtainVersion(): Double {
        val url = URL("https://github.com/chopinsong/FileLibrary/blob/master/version.txt")
        val ism = url.openStream()
        val bytes = ByteArray(1024)
        ism.read(bytes)
        val str = String(bytes, Charset.forName("utf-8"))
        Log.i("chopin" ,"ver =$str")
        return if (TextUtils.isEmpty(str)) 0.0 else str.toDouble()
    }

    fun download(context: Context, downloadUrl: String) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setDestinationInExternalPublicDir("/download/", Constant.APKNAME)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun getVersion(context: Context): String {
        try {//获得包的信息
            val packInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packInfo.versionName//获取版本
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    fun install(context: Context, apkFilePath: String) {
        val apkfile = File(apkFilePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}