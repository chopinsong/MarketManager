package com.chopin.marketmanager.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.sql.DBManager
import kotlinx.android.synthetic.main.purchase_layout.*

class PSActivity : AppCompatActivity() {
    var isP = false
    var brands = arrayOf("")
    var types = arrayOf("")
    var names = arrayOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.purchase_layout)

        isP = intent.getBooleanExtra("isP", true)

        brands = DBManager.brands().toTypedArray()
        types = DBManager.types().toTypedArray()
        names = DBManager.goodsNames().toTypedArray()
        if (brands.isNotEmpty()) {
            brand_picker.displayedValues = brands
            brand_picker.minValue = 0
            brand_picker.maxValue = brands.size - 1
        }

        if (types.isNotEmpty()) {
            type_picker.displayedValues = types
            type_picker.minValue = 0
            type_picker.maxValue = types.size - 1
        }

        if (names.isNotEmpty()) {
            name_picker.displayedValues = names
            name_picker.minValue = 0
            name_picker.maxValue = names.size - 1
        }

        commit_btn.setOnClickListener { commit() }
        add_goods_btn.setOnClickListener { startAddGoodsActivity() }
    }

    private fun startAddGoodsActivity() {
        val i = Intent(applicationContext, AddGoodsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun getCustomerName(): String {
        val name = customer_et.text.toString()
        return if (TextUtils.isEmpty(name)) "" else name
    }

    private fun getInputPrice(): Double {
        val price = price_et.text.toString()
        return if (TextUtils.isEmpty(price)) 0.0 else price.toDouble()
    }

    private fun getPurchaseCount(): Int {
        val price = price_et.text.toString()
        return if (TextUtils.isEmpty(price)) 0 else price.toInt()
    }

    private fun getSelectName(): String {
        val name = names[name_picker.value]
//        val name = name_picker.value.toString()
        return if (TextUtils.isEmpty(name)) "" else name
    }

    private fun getSelectBrand(): String {
        val brand = brands[brand_picker.value]
//        val brand = brand_picker.value.toString()
        return if (TextUtils.isEmpty(brand)) "" else brand
    }

    private fun getSelectType(): String {
        val type = types[type_picker.value]
//        val type = type_picker.value.toString()
        return if (TextUtils.isEmpty(type)) "" else type
    }

    private fun commit() {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        val inputPrice = getInputPrice()
        val purchaseCount = getPurchaseCount()
        val customerName = getCustomerName()
        val goodsId = DBManager.getGoodsName(selectBrand, selectType, selectName)

        for (i in 0 until purchaseCount) {
            DBManager.ps(PSBean(0, goodsId, inputPrice, customerName, isP))
        }
        finish()
    }


}
