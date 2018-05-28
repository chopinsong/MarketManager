package com.chopin.marketmanager.sql

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.chopin.marketmanager.bean.*
import com.chopin.marketmanager.util.Util

class DBUtil(context: Context) {
    private val db = DBHelper(context).writableDatabase

    fun purchase(b: PurchaseBean) {
        val cv = ContentValues()
        cv.put(PurchaseTable.GOODS_ID, b.goodsId)
        cv.put(PurchaseTable.PURCHASE_PRICE, b.purchasePrice)
        cv.put(PurchaseTable.CUSTOMER_NAME, b.customerName)
        cv.put(PurchaseTable.TIME, b.time)
        db.insert(PurchaseTable.NAME, null, cv)
    }

    fun shipments(b: ShipmentsBean) {
        val cv = ContentValues()
        cv.put(PurchaseTable.GOODS_ID, b.goodsId)
        cv.put(PurchaseTable.PURCHASE_PRICE, b.ShipmentsPrice)
        cv.put(PurchaseTable.CUSTOMER_NAME, b.customerName)
        cv.put(PurchaseTable.TIME, b.time)
        db.insert(PurchaseTable.NAME, null, cv)
    }

    fun purchaseList(): ArrayList<PurchaseBean> {
        val list = ArrayList<PurchaseBean>()
        val c = db.rawQuery("select * from ${PurchaseTable.NAME} ", null) ?: return list
        while (c.moveToNext()) {
            val purchaseId = c.getInt(c.getColumnIndex(PurchaseTable.PURCHASE_ID))
            val goodsId = c.getInt(c.getColumnIndex(PurchaseTable.GOODS_ID))
            val purchasePrice = c.getDouble(c.getColumnIndex(PurchaseTable.PURCHASE_PRICE))
            val customerName = c.getString(c.getColumnIndex(PurchaseTable.CUSTOMER_NAME))
            val time = c.getString(c.getColumnIndex(PurchaseTable.TIME))
            list.add(PurchaseBean(purchaseId, goodsId, purchasePrice, customerName, time))
        }
        return list
    }

    fun shipmentsList(): ArrayList<ShipmentsBean> {
        val list = ArrayList<ShipmentsBean>()
        val c = db.rawQuery("select * from ${ShipmentsTable.NAME} ", null) ?: return list
        while (c.moveToNext()) {
            val shipmentsId = c.getInt(c.getColumnIndex(ShipmentsTable.SHIPMENTS_ID))
            val goodsId = c.getInt(c.getColumnIndex(ShipmentsTable.GOODS_ID))
            val shipmentsPrice = c.getDouble(c.getColumnIndex(ShipmentsTable.SHIPMENTS_PRICE))
            val customerName = c.getString(c.getColumnIndex(ShipmentsTable.CUSTOMER_NAME))
            val time = c.getString(c.getColumnIndex(ShipmentsTable.TIME))
            list.add(ShipmentsBean(shipmentsId, goodsId, shipmentsPrice, customerName, time))
        }
        return list
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        val list = ArrayList<PurchaseCount>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${PurchaseTable.NAME}", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(PurchaseCount(id, count))
        }
        return list
    }

    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        val list = ArrayList<ShipmentsCount>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${ShipmentsTable.NAME}", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val count = c.getInt(1)
            list.add(ShipmentsCount(id, count))
        }
        return list
    }

    fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        val map = HashMap<Int, Int>()
        val c = db.rawQuery("select GoodsId,Count(GoodsId) from ${ShipmentsTable.NAME}", null)
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
        val c=db.rawQuery("select ${GoodsTable.BRAND} from ${GoodsTable.NAME} group by ${GoodsTable.BRAND}",null)
        while (c.moveToNext()){
            val brand=c.getString(0)
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
        val c=db.rawQuery("select ${GoodsTable.TYPE} from ${GoodsTable.NAME} group by ${GoodsTable.TYPE}",null)
        while (c.moveToNext()){
            val brand=c.getString(0)
            list.add(brand)
        }
//        val c = db.rawQuery("select ${TypeTable.Type_NAME} from ${TypeTable.NAME}", null)
//        while (c.moveToNext()) {
//            val brand = c.getString(c.getColumnIndex(TypeTable.Type_NAME))
//            list.add(brand)
//        }
        return list
    }

    fun goodsNames():ArrayList<String>{
        val list = ArrayList<String>()
        val c=db.query(GoodsTable.NAME, arrayOf(GoodsTable.GOODS_NAME), null,null,GoodsTable.GOODS_NAME,null,null)
        while (c.moveToNext()){
            val brand=c.getString(0)
            Log.i("chopin",brand.toString())
            list.add(brand)
        }
        return list
    }

    fun getGoodsId(selectBrand: String, selecttype: String, selectName: String): Int {
       val c= db.query(GoodsTable.NAME, arrayOf(GoodsTable.Goods_ID),"${GoodsTable.BRAND}=? and ${GoodsTable.TYPE}=? and ${GoodsTable.GOODS_NAME}=?", arrayOf(selectBrand,selecttype,selectName),null,null,null)
        if (c==null||c.count==0){
            return 0
        }
        return c.getInt(c.getColumnIndex(GoodsTable.Goods_ID))
    }

    fun getGood(id:Int):Goods{
        val c= db.rawQuery("select * from ${GoodsTable.NAME} where ${GoodsTable.Goods_ID}=$id",null)
                ?: return Goods(0,"","","",0.0)
        if(c.moveToNext()){
            val brand = c.getString(c.getColumnIndex(GoodsTable.BRAND))
            val type = c.getString(c.getColumnIndex(GoodsTable.TYPE))
            val goodsName = c.getString(c.getColumnIndex(GoodsTable.GOODS_NAME))
            val avgPrice = c.getDouble(c.getColumnIndex(GoodsTable.AVERAGE_PRICE))
            return Goods(id,goodsName,brand,type,avgPrice)
        }
        return Goods(0,"","","",0.0)
    }

    fun addGoods(g:Goods){
        val cv=ContentValues()
        cv.put(GoodsTable.BRAND,g.brand)
        cv.put(GoodsTable.TYPE,g.type)
        cv.put(GoodsTable.GOODS_NAME,g.name)
        cv.put(GoodsTable.AVERAGE_PRICE,g.avgPrice)
        db.insert(GoodsTable.NAME,null,cv)

    }

    fun PsBeans(): ArrayList<PSItemBean> {
        val list =ArrayList<PSItemBean>()
        for (purchaseBean in purchaseList()) {
            val g = getGood(purchaseBean.pGoodsId)
            list.add(PSItemBean(g,"进货",purchaseBean.purchasePrice.toString(),purchaseBean.pCustomerName,purchaseBean.pTime))
        }
        for (shipmentBean in shipmentsList()) {
            val g = getGood(shipmentBean.sGoodsId)
            list.add(PSItemBean(g,"出货",shipmentBean.ShipmentsPrice.toString(),shipmentBean.sCustomerName,shipmentBean.sTime))
        }
        return list

    }
//
//    fun addBrand(name:String){
//        val cv = ContentValues()
//        cv.put(BrandTable.BRAND_NAME, name)
//        cv.put(BrandTable.TIME, Util.time())
//        db.insert(BrandTable.NAME, null, cv)
//    }
//
//    fun addTYpe(name:String){
//        val cv = ContentValues()
//        cv.put(TypeTable.Type_NAME, name)
//        cv.put(TypeTable.TIME, Util.time())
//        db.insert(TypeTable.NAME, null, cv)
//    }

}