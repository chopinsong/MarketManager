package com.chopin.marketmanager.sql

object PSTable {
    const val NAME = "PS"
    const val PS_ID = "PSId"
    const val GOODS_ID = "GoodsId"
    const val PS_PRICE = "PSPrice"
    const val CUSTOMER_NAME = "CustomerName"
    const val IS_PURCHASE = "isPurchase"
    const val PS_COUNT = "count"
    const val IS_ENABLE = "isEnabled"
    const val TIME = "Time"
    const val PS_REMARK = "remark"

    fun getCommand(): String {
        return "CREATE TABLE $NAME($PS_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$PS_PRICE DOUBLE,$CUSTOMER_NAME text,$IS_PURCHASE INTEGER,$PS_COUNT INTEGER,$IS_ENABLE INTEGER,$PS_REMARK TEXT,$TIME TIMESTAMP(14));"
    }
}
//object PurchaseTable {
//    val NAME = "Purchase"
//    val PURCHASE_ID = "PurchaseId"
//    val GOODS_ID = "GoodsId"
//    val PURCHASE_PRICE = "PurchasePrice"
//    val CUSTOMER_NAME = "CustomerName"
//    val TIME = "Time"
//
//    fun getCommand(): String {
//        return "CREATE TABLE $NAME($PURCHASE_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$PURCHASE_PRICE DOUBLE,$CUSTOMER_NAME text,$TIME TIMESTAMP(14));"
//    }
//}
//
//object ShipmentsTable {
//    val NAME = "Shipments"
//    val SHIPMENTS_ID = "ShipmentsId"
//    val GOODS_ID = "GoodsId"
//    val SHIPMENTS_PRICE = "ShipmentsPrice"
//    val CUSTOMER_NAME = "CustomerName"
//    val TIME = "Time"
//
//    fun getCommand(): String {
//        return "CREATE TABLE $NAME($SHIPMENTS_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$SHIPMENTS_PRICE DOUBLE,${ShipmentsTable.CUSTOMER_NAME} text,$TIME TIMESTAMP(14));"
//    }
//}

object GoodsTable {
    val NAME = "Goods"
    val Goods_ID = "GoodsId"
    val GOODS_NAME = "GoodsName"
    val BRAND = "brand"
    val TYPE = "type"
    val AVERAGE_PRICE = "AveragePrice"
    val IS_ENABLE = "isEnabled"
    val TIME = "Time"
    val IMAGE_PATH = "image_path"

    fun getCommand(): String {
        return "CREATE TABLE $NAME($Goods_ID  INTEGER PRIMARY KEY," +
                "$GOODS_NAME Text,$BRAND text,$TYPE text,$AVERAGE_PRICE DOUBLE,$IS_ENABLE INTEGER,$TIME TIMESTAMP(14));"
    }
}

//object BrandTable{
//    val NAME = "Brand"
//    val BRAND_ID = "BrandId"
//    val BRAND_NAME = "BrandName"
//    val TIME = "Time"
//
//    fun getCommand(): String {
//        return "CREATE TABLE ${BrandTable.NAME}(${BrandTable.BRAND_ID}  INTEGER PRIMARY KEY," +
//                "${BrandTable.BRAND_NAME} Text,${BrandTable.TIME} TIMESTAMP(14));"
//    }
//}
//object TypeTable{
//    val NAME = "Type"
//    val Type_ID = "TypeId"
//    val Type_NAME = "TypeName"
//    val TIME = "Time"
//
//    fun getCommand(): String {
//        return "CREATE TABLE ${TypeTable.NAME}(${TypeTable.Type_ID}  INTEGER PRIMARY KEY," +
//                "${TypeTable.Type_NAME} Text,${TypeTable.TIME} TIMESTAMP(14));"
//    }
//}