package com.chopin.marketmanager.recevier

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chopin.marketmanager.util.UpdateHelper
import java.lang.ref.WeakReference

class InstallReceiver(private val weak: WeakReference<Activity>):BroadcastReceiver(){

    override fun onReceive(c: Context?, i: Intent?) {
        weak.get()?.let {
            try {
                UpdateHelper.showInstall(it)
            } catch (e: Exception) {
                System.out.print(e)
            }
        }

    }

}