package com.chopin.marketmanager.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.chopin.marketmanager.ui.PSActivity
import org.jetbrains.anko.startActivity
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.io.*


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
        context.startActivity<PSActivity>(Pair("isP",isP))
    }

}
