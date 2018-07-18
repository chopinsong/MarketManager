package com.chopin.marketmanager.bean

import com.chopin.marketmanager.util.Util
import com.chopin.marketmanager.util.time2long


class Goods(var id: Int = 0, var name: String, var brand: String, var type: String,var avgPrice: Double, var isEnabled:Boolean=true,var time: String = Util.crTime())
open class PSBean(var psId: Int, var goodsId: Int, var price: Double, var customerName: String, val isPurchase: Boolean, var count: Int, var isEnabled: Boolean =true, var time: String = Util.crTime())

//class PurchaseBean(var purchaseId:Int,var pGoodsId:Int,var purchasePrice:Double,var pCustomerName:String,var pTime:String= Util.time()):PSBean(purchaseId,pGoodsId,purchasePrice,pCustomerName,pTime)
//
//class ShipmentsBean(var shipmentsId:Int,var sGoodsId:Int,var ShipmentsPrice:Double,var sCustomerName:String,var sTime:String= Util.time()):PSBean(shipmentsId,sGoodsId,ShipmentsPrice,sCustomerName,sTime)

class PurchaseCount(var goodsId: Int, var count: Int)

class ShipmentsCount(var goodsId: Int, var count: Int)

class StockBean(var goods: Goods, var count: Int)

class PSItemBean(var g: Goods,var psId: Int, var isP: Boolean, var price: String, var customerName: String, var count: String, var time: String) {
    @Override
    fun compareTo(bean: PSItemBean): Int {
        return (time2long(time) - time2long(bean.time)).toInt()
    }
}

class PickerBean(var brand: String, var type: String, var name: String)