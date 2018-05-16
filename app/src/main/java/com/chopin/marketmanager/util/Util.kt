package com.chopin.marketmanager.util

import android.content.Context
import java.lang.ref.WeakReference

object Util {
    fun toWeak(context: Context): WeakReference<Context> {
        return WeakReference(context)
    }
}