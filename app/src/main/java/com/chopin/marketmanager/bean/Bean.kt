package com.chopin.marketmanager.bean

import com.chopin.marketmanager.util.Util

class Goods(var id:Int=0,var name:String,var brand:String,var type:String,var avgPrice:Double)
open class PSBean(var psId:Int, var goodsId:Int, var price:Double, var customerName:String,val isPurchase:Boolean, var time:String= Util.time())

//class PurchaseBean(var purchaseId:Int,var pGoodsId:Int,var purchasePrice:Double,var pCustomerName:String,var pTime:String= Util.time()):PSBean(purchaseId,pGoodsId,purchasePrice,pCustomerName,pTime)
//
//class ShipmentsBean(var shipmentsId:Int,var sGoodsId:Int,var ShipmentsPrice:Double,var sCustomerName:String,var sTime:String= Util.time()):PSBean(shipmentsId,sGoodsId,ShipmentsPrice,sCustomerName,sTime)

class PurchaseCount(var goodsId:Int, var count:Int)

class ShipmentsCount(var goodsId:Int, var count:Int)

class StockBean(var goodsId:Int,var count:Int)

class PSItemBean(var g:Goods,var pOrS:String,var price:String,var customerName: String,var time: String){
    @Override fun compareTo(bean:PSItemBean):Int{
        return (Util.time2long(time)-Util.time2long(bean.time)).toInt()
    }
}