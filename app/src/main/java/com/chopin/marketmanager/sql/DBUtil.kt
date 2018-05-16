package com.chopin.marketmanager.sql

import android.content.Context

class DBUtil(val context: Context) {
    private val helper: DBHelper = DBHelper(context)
}