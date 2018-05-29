package com.chopin.marketmanager.util

import android.content.Context
import android.content.Intent
import android.os.Build
import com.chopin.marketmanager.ui.PSActivity
import java.lang.ref.WeakReference
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
}