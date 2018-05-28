package com.chopin.marketmanager.sql

import android.content.Context
import com.chopin.marketmanager.bean.*

/**
 * Created by viking on 11/7/17.
 *
 *
 * Provider interface to create or delete
 */

object DBManager {
    private lateinit var db: DBUtil
    /**
     * init all database
     *
     * @param context The context to use.  Usually your [android.app.Application]
     * or [android.app.Activity] object.
     */
    fun initDB(context: Context) {
        db = DBUtil(context)
    }

    /**
     * delete all database
     *
     * @param context The context to use.  Usually your [android.app.Application]
     * or [android.app.Activity] object.
     */
    fun deletelDB(context: Context) {
    }

    fun purchase(b: PurchaseBean) {
        db.purchase(b)
    }

    fun shipments(b: ShipmentsBean) {
        db.shipments(b)
    }

    fun purchaseList(): ArrayList<PurchaseBean> {
     return   db.purchaseList()
    }

    fun shipmentsList(): ArrayList<ShipmentsBean> {
       return db.shipmentsList()
    }

    fun purchaseGoodsCount(): ArrayList<PurchaseCount> {
        return db.purchaseGoodsCount()
    }

    fun shipmentsGoodsCount(): ArrayList<ShipmentsCount> {
        return shipmentsGoodsCount()
    }

    fun shipmentsGoodsCountMap(): HashMap<Int, Int> {
        return shipmentsGoodsCountMap()
    }

    fun stock(): ArrayList<StockBean> {
        return stock()
    }

    fun brands():ArrayList<String>{
     return   db.brands()
    }

    fun types():ArrayList<String>{
        return db.types()
    }

    fun goodsNames():ArrayList<String> {
        return db.goodsNames()
    }

    fun getGoodsName(selectBrand: String, selecttype: String, selectName: String) :Int{
        return db.getGoodsId(selectBrand,selecttype,selectName)
    }

    fun addGoods(g:Goods){
        db.addGoods(g)
    }

    fun getPSBeans(): ArrayList<PSItemBean> {
        return db.PsBeans()
    }
//
//    fun types(): ArrayList<String> {
//       return db.types()
//    }
//
//    fun addBrand(name:String){
//       db.addBrand(name)
//    }
//
//    fun addTYpe(name:String){
//        db.addTYpe(name)
//    }

}

