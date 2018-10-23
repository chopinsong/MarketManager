package com.chopin.marketmanager.util

import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.sql.DBManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PSManager {
    fun ps(brand: String, type: String, name: String = "", count: Int, price: Double, customerName: String="", remark: String="",isP:Boolean=true,callBack:(b:PSBean)->Unit={}) {
        doAsync {
            val goodsId = DBManager.getGoodsId(brand, type, name)
            val psBean = PSBean(psId = -1, goodsId = goodsId, price = price, customerName = customerName, isPurchase = isP, count = count, remark = remark)
            val id = DBManager.ps(psBean)
            psBean.psId = id.toInt()
            uiThread {
                callBack.invoke(psBean)
            }
        }

    }
}