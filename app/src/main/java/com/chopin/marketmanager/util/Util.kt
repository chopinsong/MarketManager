package com.chopin.marketmanager.util

import android.content.Context
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun toWeak(context: Context): WeakReference<Context> {
        return WeakReference(context)
    }

    fun time(): String {
        val fm=SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }
}