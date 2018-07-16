package com.chopin.marketmanager.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.getProgressDialog
import kotlinx.android.synthetic.main.add_goods_activity.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class AddGoodsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goods_activity)
        add_goods_commit_btn.setOnClickListener { commit() }
    }

    private fun getBrand(): String {
        val str = add_goods_brand.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getType(): String {
        val str = add_goods_type.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getName(): String {
        val str = add_goods_name.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun commit() {
        val progressDialog = getProgressDialog()
        progressDialog.show(fragmentManager,"addGoodsActivity")
        val brand = getBrand()
        val type = getType()
        val name = getName()
        val avgPrice=getAvgPrice()
        async {
            val goodsId = DBManager.getGoodsId(brand, type, name)
            if (goodsId==-1){
                DBManager.addGoods(Goods(brand = brand,type = type,name = name,avgPrice = avgPrice))
            }else{
                Snackbar.make(window.decorView,"商品重复",Snackbar.LENGTH_SHORT).show()
            }
            uiThread {
                finish()
            }
        }

    }

    private fun getAvgPrice(): Double {
        val str=add_goods_avg_price.text.toString()
        return if (TextUtils.isEmpty(str)) 0.0 else str.toDouble()
    }

}