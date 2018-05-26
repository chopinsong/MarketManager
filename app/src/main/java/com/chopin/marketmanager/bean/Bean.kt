package com.chopin.marketmanager.bean

import com.chopin.marketmanager.util.Util

class Goods(var id:Int=0,var name:String,var brand:String,var type:String,var avgPrice:Double)

class PurchaseBean(var purchaseId:Int,var goodsId:Int,var purchasePrice:Double,var customerName:String,var time:String= Util.time())

class ShipmentsBean(var ShipmentsId:Int,var goodsId:Int,var ShipmentsPrice:Double,var customerName:String,var time:String= Util.time())

class PurchaseCount(var goodsId:Int, var count:Int)

class ShipmentsCount(var goodsId:Int, var count:Int)

class StockBean(var goodsId:Int,var count:Int)