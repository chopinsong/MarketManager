package com.chopin.marketmanager.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.util.Constant

/**
 * Created by viking on 11/7/17.
 *
 *
 * Create or delete DB operations
 */

class DBHelper(context: Context) : SQLiteOpenHelper(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.i(Constant.TAG, "enter DBHelper onCreate")
        db.execSQL(PSTable.getCommand())
        db.execSQL(GoodsTable.getCommand())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.i(Constant.TAG, "Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
////        db.execSQL("DROP TABLE IF EXISTS " + PSTable.NAME)
////        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.NAME)
//        onCreate(db)
    }
}
