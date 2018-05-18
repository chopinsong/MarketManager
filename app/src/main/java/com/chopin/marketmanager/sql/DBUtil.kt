package com.chopin.marketmanager.sql

import android.content.ContentValues
import android.content.Context
import com.chopin.marketmanager.bean.*

class DBUtil(context: Context) {
    private val db = DBHelper(context).writableDatabase

    fun purchase(b: PurchaseBean){
        val cv=ContentValues()
        cv.put(PurchaseTable.GOODS_ID,b.goodsId)
        cv.put(PurchaseTable.PURCHASE_PRICE,b.purchasePrice)
        cv.put(PurchaseTable.CUSTOMER_NAME,b.customerName)
        cv.put(PurchaseTable.TIME,b.time)
        db.insert(PurchaseTable.NAME,null,cv)
    }

    fun shipments(b: ShipmentsBean){
        val cv=ContentValues()
        cv.put(PurchaseTable.GOODS_ID,b.goodsId)
        cv.put(PurchaseTable.PURCHASE_PRICE,b.ShipmentsPrice)
        cv.put(PurchaseTable.CUSTOMER_NAME,b.customerName)
        cv.put(PurchaseTable.TIME,b.time)
        db.insert(PurchaseTable.NAME,null,cv)
    }

    fun purchaseList():ArrayList<PurchaseBean>{
        val list=ArrayList<PurchaseBean>()
        val c= db.rawQuery("select * from ${PurchaseTable.NAME} ",null) ?: return list
        while (c.moveToNext()){
            val purchaseId=c.getInt(c.getColumnIndex(PurchaseTable.PURCHASE_ID))
            val goodsId=c.getInt(c.getColumnIndex(PurchaseTable.GOODS_ID))
            val purchasePrice=c.getDouble(c.getColumnIndex(PurchaseTable.PURCHASE_PRICE))
            val customerName=c.getString(c.getColumnIndex(PurchaseTable.CUSTOMER_NAME))
            val time=c.getString(c.getColumnIndex(PurchaseTable.TIME))
            list.add(PurchaseBean(purchaseId,goodsId,purchasePrice,customerName,time))
        }
        return list
    }
    fun shipmentsList():ArrayList<ShipmentsBean>{
        val list=ArrayList<ShipmentsBean>()
        val c= db.rawQuery("select * from ${ShipmentsTable.NAME} ",null) ?: return list
        while (c.moveToNext()){
            val shipmentsId=c.getInt(c.getColumnIndex(ShipmentsTable.SHIPMENTS_ID))
            val goodsId=c.getInt(c.getColumnIndex(ShipmentsTable.GOODS_ID))
            val shipmentsPrice=c.getDouble(c.getColumnIndex(ShipmentsTable.SHIPMENTS_PRICE))
            val customerName=c.getString(c.getColumnIndex(ShipmentsTable.CUSTOMER_NAME))
            val time=c.getString(c.getColumnIndex(ShipmentsTable.TIME))
            list.add(ShipmentsBean(shipmentsId,goodsId,shipmentsPrice,customerName,time))
        }
        return list
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        val list=ArrayList<PurchaseCount>()
        val c=db.rawQuery("select GoodsId,Count(GoodsId) from ${PurchaseTable.NAME}",null)
        while (c.moveToNext()){
            val id=c.getInt(0)
            val count=c.getInt(1)
            list.add(PurchaseCount(id,count))
        }
        return list
    }
    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        val list=ArrayList<ShipmentsCount>()
        val c=db.rawQuery("select GoodsId,Count(GoodsId) from ${ShipmentsTable.NAME}",null)
        while (c.moveToNext()){
            val id=c.getInt(0)
            val count=c.getInt(1)
            list.add(ShipmentsCount(id,count))
        }
        return list
    }

    fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        val map=HashMap<Int,Int>()
        val c=db.rawQuery("select GoodsId,Count(GoodsId) from ${ShipmentsTable.NAME}",null)
        while (c.moveToNext()){
            val id=c.getInt(0)
            val count=c.getInt(1)
            map.put(id,count)
        }
        return map
    }

    fun stock(): ArrayList<StockBean> {
        val pcList = purchaseGoodsCount()
        val map = shipmentsGoodsCountMap()
        val stockList=ArrayList<StockBean>()
        for (purchaseCount in pcList){
                val shipmentsCount = map[purchaseCount.goodsId]?:0
                stockList.add(StockBean(purchaseCount.goodsId,purchaseCount.count-shipmentsCount))
        }
        return stockList
    }

}