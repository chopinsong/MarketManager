package com.chopin.marketmanager.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.chopin.marketmanager.util.Constant
import com.chopin.marketmanager.util.i

/**
 * Created by viking on 11/7/17.
 *
 *
 * Create or delete DB operations
 */

class DBHelper(context: Context) : SQLiteOpenHelper(context, "MarketManager.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        i("enter DBHelper onCreate")
        db.execSQL(PSTable.getCommand())
        db.execSQL(GoodsTable.getCommand())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        i("Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
////        db.execSQL("DROP TABLE IF EXISTS " + PSTable.NAME)
////        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.NAME)
//        onCreate(db)
        if (oldVersion < 4 && newVersion == 4) {
            db.execSQL("alter table ${PSTable.NAME} add remark text default ''")
        }
        if (oldVersion == 4 && newVersion == 5) {
            db.execSQL("alter table ${GoodsTable.NAME} add ${GoodsTable.IMAGE_PATH} text default ''")
        }
    }
}
