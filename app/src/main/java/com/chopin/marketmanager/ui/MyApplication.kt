package com.chopin.marketmanager.ui

import android.app.Application
import com.chopin.marketmanager.sql.DBManager

class MyApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        DBManager.initDB(applicationContext)
    }

}