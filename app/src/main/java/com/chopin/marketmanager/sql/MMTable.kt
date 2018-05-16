package com.chopin.marketmanager.sql

object PurchaseTable {
    val NAME = "Purchase"
    val PURCHASE_ID = "PurchaseId"
    val GOODS_ID = "GoodsId"
    val PURCHASE_PRICE = "PurchasePrice"
    val CUSTOMER_NAME = "CustomerName"
    val TIME = "Time"

    fun getCommand(): String {
        return "CREATE TABLE $NAME($PURCHASE_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$PURCHASE_PRICE DOUBLE,$CUSTOMER_NAME text,$TIME TIMESTAMP(14));"
    }
}

object ShipmentsTable {
    val NAME = "Shipments"
    val SHIPMENTS_ID = "ShipmentsId"
    val GOODS_ID = "GoodsId"
    val SHIPMENTS_PRICE = "ShipmentsPrice"
    val CUSTOMER_NAME = "CustomerName"
    val TIME = "Time"

    fun getCommand(): String {
        return "CREATE TABLE $NAME($SHIPMENTS_ID  INTEGER PRIMARY KEY,$GOODS_ID INTEGER,$SHIPMENTS_PRICE DOUBLE,${ShipmentsTable.CUSTOMER_NAME} text,$TIME TIMESTAMP(14));"
    }
}

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