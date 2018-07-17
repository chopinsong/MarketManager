package com.chopin.marketmanager.util

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import org.jetbrains.anko.async
import java.lang.ref.WeakReference


object UpdateHelper {
    val versionUrl = "https://raw.githubusercontent.com/chopinsong/FileLibrary/master/version.txt"
    val apkUrl = "https://raw.githubusercontent.com/chopinsong/FileLibrary/master/MarketManager.apk"
    var remoteVersion = 0.0
    fun check(context: Context): Boolean {
        val curVersion = getVersion(context)
        remoteVersion = obtainVersion()
        i("curVersion=$curVersion   remoteVersion=$remoteVersion")
        return remoteVersion > curVersion
    }

    fun showInstall(activity: Activity) {
        Snackbar.make(activity.window.decorView, "下载完成，是否马上安装", Snackbar.LENGTH_LONG).setAction("安装") {
            install(activity.applicationContext, "${Environment.getExternalStorageDirectory()}${File.separator}download${File.separator + Constant.APKNAME + remoteVersion}")
        }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                val alarmManager = activity.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent2 = Intent().setAction(Constant.INSTALL_ACTION)
                val uploadIntent = PendingIntent.getService(activity.applicationContext, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 60 * 60, AlarmManager.INTERVAL_DAY, uploadIntent)
            }
        }).show()
    }

    fun showDownload(activity: Activity, function: (c: Context) -> Unit) {
        Snackbar.make(activity.window.decorView, "有新版本，是否下载", Snackbar.LENGTH_LONG).setAction("下载") {
            function(activity.applicationContext)
        }.show()
    }

    fun download(context: Context, downloadUrl: String = apkUrl, todo: (c: Context) -> Unit) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setDestinationInExternalPublicDir("/download/", Constant.APKNAME + remoteVersion)
        request.setTitle("销售管理")
        request.setDescription("销售管理更新版本")
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = downloadManager.enqueue(request)
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, i: Intent?) {
                val downloadId = i?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    if (c != null) {
                        todo(c)
                        c.unregisterReceiver(this)
                    }
                }
            }

        }
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun obtainVersion(): Double {
        val url = URL(versionUrl)
        val ism = url.openStream()
        val bytes = ByteArray(1024)
        ism.read(bytes)
        val str = String(bytes, Charset.forName("utf-8"))
        Log.i("chopin", "ver =$str")
        return if (TextUtils.isEmpty(str)) 0.0 else str.toDouble()
    }

    fun obtainVersion2(): String {
        val url = URL(versionUrl)
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        val s: String = reader.readLine()
        reader.close()
        return s
    }


    fun getVersion(context: Context): Double {
        try {//获得包的信息
            val packInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packInfo.versionName.toDouble()//获取版本
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1.0
    }

    fun install(context: Context, apkFilePath: String) {
        val apkfile = File(apkFilePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun readFileByUrl(urlStr: String): String {
        var res = ""
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            //设置超时间为3秒
            conn.connectTimeout = 3 * 1000
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
            //得到输入流
            val inputStream = conn.inputStream
            res = readInputStream(inputStream)
        } catch (e: Exception) {
            Log.i("chopin", "通过url地址获取文本内容失败 Exception：$e")
        }

        return res
    }

    /**
     * 从输入流中获取字符串
     * @param inputStream
     * @return
     * @throws IOException
     */
    fun readInputStream(inputStream: InputStream): String {
        val buffer = ByteArray(1024)
        var len = 0
        val bos = ByteArrayOutputStream()
        try {
            var read: Int = -1
            inputStream.use { input ->
                bos.use {
                    while ({ read = input.read();read }() != -1) {
                        it.write(read)
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        bos.close()
        return String(bos.toByteArray(), Charset.forName("utf-8"))
    }

    fun update(weak:WeakReference<Activity>) {
        async {
            weak.get()?.let {
                val ac=it
                if (UpdateHelper.check(it.applicationContext)) {
                    UpdateHelper.showDownload(ac) {
                        UpdateHelper.download(ac) {
                            UpdateHelper.showInstall(ac)
                        }
                    }
                }
            }

        }
    }


}