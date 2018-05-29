package com.chopin.marketmanager.sql

object PSTable{
    val NAME = "PS"
    val PS_ID = "PSId"
    val GOODS_ID = "GoodsId"
    val PS_PRICE = "PSPrice"
    val CUSTOMER_NAME = "CustomerName"
    val IS_PURCHASE="isPURCHASE"
    val PS_COUNT="count"
    val TIME = "Time"

    fun getCommand(): String {
        return "CREATE TABLE $NAME($PS_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$PS_PRICE DOUBLE,$CUSTOMER_NAME text,$IS_PURCHASE INTEGER,$PS_COUNT INTEGER,$TIME TIMESTAMP(14));"
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
    val TIME = "Time"

    fun getCommand(): String {
        return "CREATE TABLE ${GoodsTable.NAME}(${GoodsTable.Goods_ID}  INTEGER PRIMARY KEY," +
                "${GoodsTable.GOODS_NAME} Text,${GoodsTable.BRAND} text,$TYPE text,$AVERAGE_PRICE DOUBLE,${GoodsTable.TIME} TIMESTAMP(14));"
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