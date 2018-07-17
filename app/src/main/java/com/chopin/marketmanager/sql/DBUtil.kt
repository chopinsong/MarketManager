package com.chopin.marketmanager.sql

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.chopin.marketmanager.bean.*
import com.chopin.marketmanager.util.i

class DBUtil(context: Context) {
    private val db = DBHelper(context).writableDatabase

    fun ps(b: PSBean): Long {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, if (b.isPurchase) 1 else 0)
        cv.put(PSTable.PS_COUNT, b.count)
        cv.put(PSTable.IS_ENABLE, 1)
        return db.insert(PSTable.NAME, null, cv)
    }

    fun purchase(b: PSBean) {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, 1)
        cv.put(PSTable.IS_ENABLE, 1)
        db.insert(PSTable.NAME, null, cv)
    }

    fun shipments(b: PSBean) {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_ENABLE, 1)
        cv.put(PSTable.IS_PURCHASE, 0)
        db.insert(PSTable.NAME, null, cv)
    }

    fun purchaseList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
//        val c=db.rawQuery("select * from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=1",null )null
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_PURCHASE}=? and ${PSTable.IS_ENABLE}=?", arrayOf(1.toString(), 1.toString()), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(purchaseId, goodsId, purchasePrice, customerName, true, count, true, time))
        }
        return list
    }

    fun shipmentsList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_PURCHASE}=? and ${PSTable.IS_ENABLE}=?", arrayOf(0.toString(), 1.toString()), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(purchaseId, goodsId, purchasePrice, customerName, false, count, true, time))
        }
        return list
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        val list = ArrayList<PurchaseCount>()
        val c = db.rawQuery("select GoodsId,sum(${PSTable.PS_COUNT}) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=1 and ${PSTable.IS_ENABLE}=1", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(PurchaseCount(id, count))
        }
        return list
    }

    fun psList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.rawQuery("select * from ${PSTable.NAME} where ${PSTable.IS_ENABLE} =1" , null, null)
                ?: return list
        while (c.moveToNext()) {
            val psId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val psPrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val isPurchase = c.getInt(c.getColumnIndex(PSTable.IS_PURCHASE))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            val psBean = PSBean(psId, goodsId, psPrice, customerName, isPurchase == 1, count, true, time)
            list.add(psBean)
        }
        c.close()
        return list
    }

    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        val list = ArrayList<ShipmentsCount>()
        val c = db.rawQuery("select GoodsId,sum(${PSTable.PS_COUNT}) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0 and ${PSTable.IS_ENABLE}=1", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(ShipmentsCount(id, count))
        }
        return list
    }

    fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        val map = HashMap<Int, Int>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0 and ${PSTable.IS_ENABLE}=1", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            map.put(id, count)
        }
        return map
    }

    fun stock(): ArrayList<StockBean> {
        val pcList = purchaseGoodsCount()
        val map = shipmentsGoodsCountMap()
        val stockList = ArrayList<StockBean>()
        for (purchaseCount in pcList) {
            val shipmentsCount = map[purchaseCount.goodsId] ?: 0
            stockList.add(StockBean(purchaseCount.goodsId, purchaseCount.count - shipmentsCount))
        }
        return stockList
    }

    fun brands(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where") else where
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.BRAND} from ${GoodsTable.NAME} $w group by ${GoodsTable.BRAND}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
        return list
    }

    fun types(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where") else where
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.TYPE} from ${GoodsTable.NAME} $w group by ${GoodsTable.TYPE}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
        return list
    }

    fun goodsNames(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where") else where
        val list = ArrayList<String>()
        val c = db.query(GoodsTable.NAME, arrayOf(GoodsTable.GOODS_NAME), w, null, GoodsTable.GOODS_NAME, null, null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            Log.i("chopin", brand.toString())
            list.add(brand)
        }
        return list
    }

    fun getGoodsId(selectBrand: String, selecttype: String, selectName: String): Int {
        val c = db.query(GoodsTable.NAME, null, "${GoodsTable.BRAND}=? and ${GoodsTable.TYPE}=? and ${GoodsTable.GOODS_NAME}=?", arrayOf(selectBrand, selecttype, selectName), null, null, null)
        if (c == null || c.count == 0) {
            return -1
        }
        if (c.moveToNext()) {
            return c.getInt(c.getColumnIndex(GoodsTable.Goods_ID))
        }
        return -1
    }

    fun getGood(id: Int): Goods {
        val c = db.rawQuery("select * from ${GoodsTable.NAME} where ${GoodsTable.Goods_ID}=$id ", null)
                ?: return Goods(0, "", "", "", 0.0)
        if (c.moveToNext()) {
            val brand = c.getString(c.getColumnIndex(GoodsTable.BRAND))
            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
            val goodsName = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
            val avgPrice = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
            return Goods(id, goodsName, brand, type, avgPrice)
        }
        c.close()
        return Goods(0, "", "", "", 0.0)
    }

    fun addGoods(g: Goods) {
        val cv = ContentValues()
        cv.put(GoodsTable.BRAND, g.brand)
        cv.put(GoodsTable.TYPE, g.type)
        cv.put(GoodsTable.GOODS_NAME, g.name)
        cv.put(GoodsTable.AVERAGE_PRICE, g.avgPrice)
        db.insert(GoodsTable.NAME, null, cv)

    }

    fun psBeans(): ArrayList<PSItemBean> {
        val list = ArrayList<PSItemBean>()
        for (b in psList()) {
            val g = getGood(b.goodsId)
            list.add(PSItemBean(g, b.isPurchase, b.price.toString(), b.customerName, b.count.toString(), b.time))
        }
        return list
    }

    fun goodsCountLeft(goodsId: Int): Int {
        val c = db.rawQuery("select count(${PSTable.PS_ID}) from ${PSTable.NAME} where ${PSTable.GOODS_ID}=$goodsId and ${PSTable.IS_PURCHASE}=1", null)
        c.moveToNext()
        val purchaseCount = c.getInt(0)
        c.close()
        val c2 = db.rawQuery("select count(${PSTable.PS_ID}) from ${PSTable.NAME} where ${PSTable.GOODS_ID}=$goodsId and ${PSTable.IS_PURCHASE}=0", null)
        c2.moveToNext()
        val shipmentCount = c2.getInt(0)
        c2.close()
        return purchaseCount - shipmentCount
    }

    fun setAllDisable() {
        val cv = ContentValues()
        cv.put(PSTable.IS_ENABLE, 0)
        db.update(PSTable.NAME, cv, null, null)
    }

    fun setPSEnable(psId: Int, b: Boolean): Int {
        val cv = ContentValues()
        cv.put(PSTable.IS_ENABLE, if (b) 1 else 0)
        return db.update(PSTable.NAME, cv, "${PSTable.PS_ID}=?", arrayOf(psId.toString()))
    }


}