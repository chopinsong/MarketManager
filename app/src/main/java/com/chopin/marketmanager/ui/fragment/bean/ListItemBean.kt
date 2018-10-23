package com.chopin.marketmanager.ui.fragment.bean

data class PSPageItem(val id: String, val content: String, val details: String) {
    override fun toString(): String = content
}

data class StockItem(val id: String, val content: String, val details: String) {
    override fun toString(): String = content
}