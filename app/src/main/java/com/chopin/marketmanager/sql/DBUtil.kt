package com.chopin.marketmanager.sql

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.chopin.marketmanager.bean.*
import com.chopin.marketmanager.util.Util
import com.chopin.marketmanager.util.i
import com.chopin.marketmanager.util.toPSItemBean
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        cv.put(PSTable.PS_REMARK, b.remark)
        cv.put(PSTable.IS_ENABLE, 1)
        return db.insert(PSTable.NAME, null, cv)
    }

    fun purchaseList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_PURCHASE}=? and ${PSTable.IS_ENABLE}=?", arrayOf(1.toString(), 1.toString()), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val remark = c.getString(c.getColumnIndex(PSTable.PS_REMARK))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(purchaseId, goodsId, purchasePrice, customerName, true, count, true, remark, time))
        }
        c.close()
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
            val remark = c.getString(c.getColumnIndex(PSTable.PS_REMARK))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            list.add(PSBean(psId = purchaseId, goodsId = goodsId, price = purchasePrice, customerName = customerName, isPurchase = false, count = count, isEnabled = true, remark = remark, time = time))
        }
        c.close()
        return list
    }


    fun psList(): ArrayList<PSBean> {
        val list = ArrayList<PSBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_ENABLE}=?", arrayOf("1"), null, null, null)
                ?: return list
        while (c.moveToNext()) {
            val psId = c.getInt(c.getColumnIndex(PSTable.PS_ID))
            val goodsId = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val customerName = c.getString(c.getColumnIndex(PSTable.CUSTOMER_NAME))
            val psPrice = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val isPurchase = c.getInt(c.getColumnIndex(PSTable.IS_PURCHASE))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val remark = c.getString(c.getColumnIndex(PSTable.PS_REMARK))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            val psBean = PSBean(psId = psId, goodsId = goodsId, price = psPrice, customerName = customerName, isPurchase = (isPurchase == 1), count = count, isEnabled = true, remark = remark, time = time)
            list.add(psBean)
        }
        c.close()
        return list
    }

    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        val list = ArrayList<ShipmentsCount>()
        val c = db.rawQuery("select ${PSTable.GOODS_ID},sum(${PSTable.PS_COUNT}) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0 and ${PSTable.IS_ENABLE}=1 group by ${PSTable.GOODS_ID}", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(ShipmentsCount(id, count))
        }
        c.close()
        return list
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        val list = ArrayList<PurchaseCount>()
        val c = db.rawQuery("select ${PSTable.GOODS_ID},sum(${PSTable.PS_COUNT}) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=1 and ${PSTable.IS_ENABLE}=1 group by ${PSTable.GOODS_ID}", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(PurchaseCount(id, count))
        }
        c.close()
        return list
    }

    fun goodsProfit(): ArrayList<ProfitBean> {
        val l = ArrayList<ProfitBean>()
        val c = db.query(PSTable.NAME, null, "${PSTable.IS_ENABLE}=?", arrayOf("1"), null, null, null)
        while (c.moveToNext()) {
            val id = c.getInt(c.getColumnIndex(PSTable.GOODS_ID))
            val count = c.getInt(c.getColumnIndex(PSTable.PS_COUNT))
            val price = c.getDouble(c.getColumnIndex(PSTable.PS_PRICE))
            val time = c.getString(c.getColumnIndex(PSTable.TIME))
            val isP = c.getInt(c.getColumnIndex(PSTable.IS_PURCHASE))
            val good = getGood(id)
            val split = time.split(" ")[0].split("-")
            val pb = ProfitBean(good, count * price, split[0].toInt(),split[1].toInt(),isP==1)
            l.add(pb)
        }
        c.close()
        return l
    }

    private fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        val map = HashMap<Int, Int>()
        val c = db.rawQuery("select ${PSTable.GOODS_ID},sum(${PSTable.PS_COUNT}) from ${PSTable.NAME} where ${PSTable.IS_PURCHASE}=0 and ${PSTable.IS_ENABLE}=1 group by ${PSTable.GOODS_ID}", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            map[id] = count
        }
        c.close()
        return map
    }


    fun stock(): ArrayList<StockBean> {
        val pcList = purchaseGoodsCount()
        val map = shipmentsGoodsCountMap()
        val stockList = arrayListOf<StockBean>()
        val stockIds = arrayListOf<Int>()
        for (p in pcList) {
            stockIds.add(p.goodsId)
            stockList.add(StockBean(getGood(p.goodsId), p.count - (map[p.goodsId] ?: 0)))
        }
        val goodsList = goods()
        for (goods in goodsList) {
            if (!stockIds.contains(goods.id)) {
                stockList.add(StockBean(goods, 0))
            }
        }
        i("list=$stockList")
        return stockList
    }

    fun stockMap(): HashMap<Int, Int> {
        val pcList = purchaseGoodsCount()
        val map = shipmentsGoodsCountMap()
        val stockMap = HashMap<Int, Int>()
        for (p in pcList) {
            stockMap[p.goodsId] = p.count - (map[p.goodsId] ?: 0)
        }
        i("map=$stockMap")
        return stockMap
    }

    fun goodsCountLeft(goodsId: Int): Int {
        return stockMap()[goodsId] ?: 0
    }

    fun brands(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where") else where
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.BRAND} from ${GoodsTable.NAME} $w group by ${GoodsTable.BRAND}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
        c.close()
        return list
    }

    fun types(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where ") else where
        val list = ArrayList<String>()
        val c = db.rawQuery("select ${GoodsTable.TYPE} from ${GoodsTable.NAME} $w group by ${GoodsTable.TYPE}", null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            list.add(brand)
        }
        c.close()
        return list
    }

    fun goodsNames(where: String = ""): ArrayList<String> {
        val w = if (where != "") (" where $where") else where
        val list = ArrayList<String>()
        val c = db.query(GoodsTable.NAME, arrayOf(GoodsTable.GOODS_NAME), w, null, null, null, null)
        while (c.moveToNext()) {
            val brand = c.getString(0)
            Log.i("chopin", brand.toString())
            list.add(brand)
        }
        c.close()
        return list
    }

//    class Goods(var id: Int = 0, var remark: String, var brand: String, var type: String, var avgPrice: Double=0.0, var isEnabled: Boolean = true, var time: String = Util.crTime()):Serializable

    fun goods(): ArrayList<Goods> {
        val list = ArrayList<Goods>()
        val c = db.query(GoodsTable.NAME, null, "${GoodsTable.IS_ENABLE}=?", arrayOf("1"), null, null, null)
        while (c.moveToNext()) {
            val id = c.getInt(c.getColumnIndex(GoodsTable.Goods_ID))
            val b = c.getString(c.getColumnIndex(GoodsTable.BRAND))
            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
            val n = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
            val p = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
            val t = c.getString(c.getColumnIndex(GoodsTable.TIME))
            list.add(Goods(id, n, b, type, p, true, t))
        }
        c.close()
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
        c.close()
        return -1
    }

    fun getGood(id: Int): Goods {
        val c = db.rawQuery("select * from ${GoodsTable.NAME} where ${GoodsTable.Goods_ID}=$id ", null)
                ?: return Goods(-1, "", "", "", 0.0)
        if (c.moveToNext()) {
            val brand = c.getString(c.getColumnIndex(GoodsTable.BRAND))
            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
            val goodsName = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
            val avgPrice = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
            return Goods(id, goodsName, brand, type, avgPrice)
        }
        c.close()
        return Goods(-1, "", "", "", 0.0)
    }

    fun addGoods(g: Goods): Long {
        val cv = ContentValues()
        cv.put(GoodsTable.BRAND, g.brand)
        cv.put(GoodsTable.TYPE, g.type)
        cv.put(GoodsTable.GOODS_NAME, g.remark)
        cv.put(GoodsTable.AVERAGE_PRICE, g.avgPrice)
        cv.put(GoodsTable.IS_ENABLE, 1)
        cv.put(GoodsTable.TIME, Util.crTime())
        return db.insert(GoodsTable.NAME, null, cv)

    }

    fun psBeans(): ArrayList<PSItemBean> {
        val list = ArrayList<PSItemBean>()
        for (b in psList()) {
            list.add(b.toPSItemBean())
        }
        return list
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

    fun updatePS(b: PSBean): Int {
        val cv = ContentValues()
        cv.put(PSTable.GOODS_ID, b.goodsId)
        cv.put(PSTable.PS_PRICE, b.price)
        cv.put(PSTable.CUSTOMER_NAME, b.customerName)
        cv.put(PSTable.TIME, b.time)
        cv.put(PSTable.IS_PURCHASE, if (b.isPurchase) 1 else 0)
        cv.put(PSTable.PS_COUNT, b.count)
        cv.put(PSTable.PS_REMARK, b.remark)
        cv.put(PSTable.IS_ENABLE, 1)
        return db.update(PSTable.NAME, cv, "${PSTable.PS_ID}=?", arrayOf(b.psId.toString()))
    }


}