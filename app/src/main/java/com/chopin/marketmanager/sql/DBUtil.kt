package com.chopin.marketmanager.sql

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.chopin.marketmanager.bean.*

class DBUtil(context: Context) {
    private val db = DBHelper(context).writableDatabase

    fun ps(b: PSBean) {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, if (b.isPurchase) 1 else 0)
        db.insert(PSTable.NAME, null, cv)
    }

    fun purchase(b: PSBean) {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, 1)
        db.insert(PSTable.NAME, null, cv)
    }

    fun shipments(b: PSBean) {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, 0)
        db.insert(PSTable.NAME, null, cv)
    }

    fun purchaseList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_PURCHASE}=?", arrayOf(1.toString()), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(purchaseId, goodsId, purchasePrice, customerName, true, time))
        }
        return list
    }

    fun shipmentsList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_PURCHASE}=?", arrayOf(0.toString()), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(purchaseId, goodsId, purchasePrice, customerName, false, time))
        }
        return list
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        val list = ArrayList<PurchaseCount>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=1", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(PurchaseCount(id, count))
        }
        return list
    }

    fun psList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, null, null, null, null, null) ?: return list
        while (c.moveToNext()) {
            val psId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val psPrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val isPurchase = c.getInt(c.getColumnIndex(PSTable.IS_PURCHASE))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(psId, goodsId, psPrice, customerName, isPurchase == 1, time))
        }
        return list
    }

    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        val list = ArrayList<ShipmentsCount>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(ShipmentsCount(id, count))
        }
        return list
    }

    fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        val map = HashMap<Int, Int>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0", null)
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

    fun brands(): ArrayList<String> {
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.BRAND} from ${GoodsTable.NAME} group by ${GoodsTable.BRAND}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
//        val c = db.rawQuery("select ${BrandTable.BRAND_NAME} from ${BrandTable.NAME}", null)
//        while (c.moveToNext()) {
//            val brand = c.getString(c.getColumnIndex(BrandTable.BRAND_NAME))
//            list.add(brand)
//        }
        return list
    }

    fun types(): ArrayList<String> {
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.TYPE} from ${GoodsTable.NAME} group by ${GoodsTable.TYPE}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
//        val c = db.rawQuery("select ${TypeTable.Type_NAME} from ${TypeTable.NAME}", null)
//        while (c.moveToNext()) {
//            val brand = c.getString(c.getColumnIndex(TypeTable.Type_NAME))
//            list.add(brand)
//        }
        return list
    }

    fun goodsNames(): ArrayList<String> {
        val list = ArrayList<String>()
        val c = db.query(GoodsTable.NAME, arrayOf(GoodsTable.GOODS_NAME), null, null, GoodsTable.GOODS_NAME, null, null)
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
            return 0
        }
        if (c.moveToNext()) {
            return c.getInt(c.getColumnIndex(GoodsTable.Goods_ID))
        }
        return 0
    }

    fun getGood(id: Int): Goods {
        val c = db.rawQuery("select * from ${GoodsTable.NAME} where ${GoodsTable.Goods_ID}=$id", null)
                ?: return Goods(0, "", "", "", 0.0)
        if (c.moveToNext()) {
            val brand = c.getString(c.getColumnIndex(GoodsTable.BRAND))
            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
            val goodsName = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
            val avgPrice = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
            return Goods(id, goodsName, brand, type, avgPrice)
        }
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
            list.add(PSItemBean(g, if (b.isPurchase) "进货" else "出货", b.price.toString(), b.customerName, b.time))
        }
        return list
    }


}