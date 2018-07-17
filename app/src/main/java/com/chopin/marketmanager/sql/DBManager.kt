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

    fun ps(b:PSBean): Long {
       return db.ps(b)
    }
    fun purchase(b: PSBean) {
        db.purchase(b)
    }

    fun shipments(b: PSBean) {
        db.shipments(b)
    }

    fun purchaseList(): ArrayList<PSBean> {
     return   db.purchaseList()
    }

    fun shipmentsList(): ArrayList<PSBean> {
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

    fun brands(where:String=""):ArrayList<String>{
     return   db.brands()
    }

    fun types(where:String=""):ArrayList<String>{
        return db.types(where)
    }

    fun goodsNames(where:String=""):ArrayList<String> {
        return db.goodsNames(where)
    }

    fun getGoodsId(selectBrand: String, selectType: String, selectName: String) :Int{
        return db.getGoodsId(selectBrand,selectType,selectName)
    }
    fun getGoodsId(goods:Goods) :Int{
        return db.getGoodsId(goods.brand,goods.type,goods.name)
    }
    fun addGoods(g:Goods){
        db.addGoods(g)
    }

    fun getPSBeans(): ArrayList<PSItemBean> {
        return db.psBeans()
    }

    fun psList(): ArrayList<PSBean> {
        return db.psList()
    }

    fun getGoodsCountLeft(goodsId: Int) :Int{
        return db.goodsCountLeft(goodsId)
    }

    fun setAllDisable() {
        db.setAllDisable()
    }

    fun setPSEnable(psId:Int,b:Boolean): Int {
       return db.setPSEnable(psId,b)
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

