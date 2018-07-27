package com.chopin.marketmanager.bean

import com.chopin.marketmanager.util.Util
import com.chopin.marketmanager.util.time2long
import java.io.Serializable
import java.util.*


class Goods(var id: Int = 0, var remark: String, var brand: String, var type: String, var avgPrice: Double = 0.0, var isEnabled: Boolean = true, var time: String = Util.crTime()) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (other is Goods){
            return other.brand==this.brand&&other.type==this.type&&other.remark==this.remark&&other.avgPrice==this.avgPrice&&other.isEnabled==this.isEnabled&&other.time==this.time
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + remark.hashCode()
        result = 31 * result + brand.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + avgPrice.hashCode()
        result = 31 * result + isEnabled.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

    fun contains(s:String):Boolean{
        return remark.contains(s)||brand.contains(s)||type.contains(s)||avgPrice.toString().contains(s)
    }
}
open class PSBean(var psId: Int, var goodsId: Int, var price: Double, var customerName: String, val isPurchase: Boolean, var count: Int, var isEnabled: Boolean = true, var remark: String = "", var time: String = Util.crTime())

//class PurchaseBean(var purchaseId:Int,var pGoodsId:Int,var purchasePrice:Double,var pCustomerName:String,var pTime:String= Util.time()):PSBean(purchaseId,pGoodsId,purchasePrice,pCustomerName,pTime)
//
//class ShipmentsBean(var shipmentsId:Int,var sGoodsId:Int,var ShipmentsPrice:Double,var sCustomerName:String,var sTime:String= Util.time()):PSBean(shipmentsId,sGoodsId,ShipmentsPrice,sCustomerName,sTime)

class PurchaseCount(var goodsId: Int, var count: Int)

class ShipmentsCount(var goodsId: Int, var count: Int)

class StockBean(var goods: Goods, var count: Int)

class PSItemBean(var g: Goods, var psId: Int, var isP: Boolean, var price: String, var customerName: String, var count: String, var remark: String = "", var time: String) : Serializable {
    @Override
    fun compareTo(bean: PSItemBean): Int {
        return (time2long(time) - time2long(bean.time)).toInt()
    }

    fun contains(s:String):Boolean{
        return g.contains(s)||price.contains(s)||customerName.contains(s)||count.contains(s)||remark.contains(s)
    }
}

class ProfitBean(var g: Goods, var price: Double, var year:Int,var month:Int,var isP: Boolean)

class PickerBean(var brand: String, var type: String, var name: String)