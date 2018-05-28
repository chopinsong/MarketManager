package com.chopin.marketmanager.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
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
        db.execSQL(PurchaseTable.getCommand())
        db.execSQL(ShipmentsTable.getCommand())
        db.execSQL(GoodsTable.getCommand())
//        db.execSQL(BrandTable.getCommand())
//        db.execSQL(TypeTable.getCommand())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Logs that the database is being upgraded
        Log.i(Constant.TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data")
        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + PurchaseTable.NAME)
        db.execSQL("DROP TABLE IF EXISTS " + ShipmentsTable.NAME)
        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.NAME)
//        db.execSQL("DROP TABLE IF EXISTS " + BrandTable.NAME)
//        db.execSQL("DROP TABLE IF EXISTS " + TypeTable.NAME)
        // Recreates the database with a new version
        onCreate(db)
    }
}
