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
//        val psList = ArrayList<PSBean>()
//        val c = db.rawQuery("select * from ${PSTable.NAME}",null,null)
//        while (c.moveToNext()){
//            val psId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
//            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
//            val price = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
//            val name = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
//            val isP = c.getInt(c.getColumnIndex(PSTable.IS_PURCHASE))
//            val psCount = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
//            val time = c.getString(c.getColumnIndex(PSTable.TIME))
//            psList.add(PSBean(psId,goodsId,price,name,isP==1,psCount,true,time))
//        }
//        c.close()
//        val gList = ArrayList<Goods>()
//        val c2 = db.rawQuery("select * from ${GoodsTable.NAME}",null,null)
//        while (c.moveToNext()){
//            val gId = c.getInt(c.getColumnIndex(GoodsTable.Goods_ID))
//            val goodsName = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
//            val brand = c.getString(c.getColumnIndex(GoodsTable.BRAND))
//            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
//            val avgPrice = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
//            val time = c.getString(c.getColumnIndex(GoodsTable.TIME))
//            gList.add(Goods(gId,goodsName,brand,type,avgPrice,true,time))
//        }
//        c2.close()
        db.execSQL("DROP TABLE IF EXISTS " + PSTable.NAME)
        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.NAME)

        onCreate(db)
    }
}
