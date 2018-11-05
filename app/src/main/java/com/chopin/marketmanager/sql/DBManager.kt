package com.chopin.marketmanager.sql

import android.content.Context
import com.chopin.marketmanager.bean.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

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
    fun deletelDB(weak:WeakReference<Context>) {
    }

    fun ps(b:PSBean): Long {
       return db.ps(b)
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
        return db.stock()
    }

    fun stockMap(): HashMap<Int, Int> {
        return db.stockMap()
    }

    fun brands(where:String=""):ArrayList<String>{
     return   db.brands(where)
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
        return db.getGoodsId(goods.brand,goods.type,goods.remark)
    }
    fun addGoods(g:Goods): Long {
      return  db.addGoods(g)
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

    fun getGoodsCountLeft(g:Goods):Int{
        return db.goodsCountLeft(getGoodsId(g))
    }

    fun setAllPSDisable(): Int {
        return db.setAllPSDisable()
    }

    fun setPSEnable(psId:Int,b:Boolean): Int {
       return db.setPSEnable(psId,b)
    }

    fun getGoodsInfo(goodsId: Int): Goods {
        return db.getGood(goodsId)
    }

    fun updatePS(b: PSBean): Int {
        return db.updatePS(b)
    }

    fun profits(): ArrayList<ProfitBean> {
        return db.goodsProfit()
    }

    fun setAllGoodsDisable(): Int {
        return db.setAllGoodsDisable()
    }

    fun goods(): ArrayList<Goods> {
        return db.goods()
    }

    fun setGoodsEnable(id: Int, b: Boolean): Int {
        return db.setGoodsEnable(id,b)
    }

    fun updateGoods(goods: Goods): Int {
        return db.updateGoods(goods)
    }

}

