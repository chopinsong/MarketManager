package com.chopin.marketmanager.util

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import com.chopin.marketmanager.BuildConfig
import com.chopin.marketmanager.util.Constant.IS_DOWNLOAD
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


object UpdateHelper {
    private const val versionUrl = "https://raw.githubusercontent.com/chopinsong/FileLibrary/master/version.txt"
    private const val apkUrl = "https://raw.githubusercontent.com/chopinsong/FileLibrary/master/MarketManager.apk"
    var remoteVersion = 0.0
    fun check(context: Context, onChecked: (Boolean) -> Unit = {}) {
        doAsync {
            val curVersion = getVersion(context)
            remoteVersion = obtainVersion()
            i("curVersion=$curVersion   remoteVersion=$remoteVersion")
            uiThread {
                onChecked.invoke(remoteVersion > curVersion)
            }
        }
    }

    fun showInstall(activity: Activity) {
        Snackbar.make(activity.window.decorView, "下载完成，是否马上安装", Snackbar.LENGTH_LONG).setAction("安装") {
            install(activity.applicationContext, "${Environment.getExternalStorageDirectory()}${File.separator}download${File.separator + Constant.APK_NAME + remoteVersion+".apk"}")
        }.show()
    }

    private fun showDownload(activity: Activity, function: (c: Context) -> Unit) {
        Snackbar.make(activity.window.decorView, "有新版本${remoteVersion}，是否下载", Snackbar.LENGTH_LONG).setAction("下载") {
            function(activity.applicationContext)
        }.show()
    }

    private fun download(context: Context, downloadUrl: String = apkUrl, todo: (c: Context) -> Unit) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setDestinationInExternalPublicDir("/download/", Constant.APK_NAME + remoteVersion+".apk")
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
        val apkFile = File(apkFilePath)
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
//        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive")
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        context.setConfig(IS_DOWNLOAD, false)
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
        ByteArray(1024)
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

    fun update(weak: WeakReference<Activity>) {
        weak.get()?.let { act ->
            check(act) { isD ->
                if (isD) {
                    val isDownload = act.getConfig(IS_DOWNLOAD) ?: false
                    if (isDownload) {
                        showInstall(act)
                    } else {
                        showDownload(act) {
                            download(act) {
                                act.setConfig(IS_DOWNLOAD, true)
                                showInstall(act)
                            }
                        }
                    }
                }
            }
        }
    }
}