package com.chopin.marketmanager.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.NumberPicker
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.sql.DBManager
import kotlinx.android.synthetic.main.purchase_layout.*

class PSActivity : AppCompatActivity(){

    var isP = false
    var brands = arrayOf("")
    var types = arrayOf("")
    var names = arrayOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.purchase_layout)

        isP = intent.getBooleanExtra("isP", true)

        updateBrandTypeName()

        commit_btn.setOnClickListener { commit() }
        add_goods_btn.setOnClickListener { startAddGoodsActivity() }
        type_picker.setOnValueChangedListener { picker, oldVal, newVal ->
            val goodsId = DBManager.getGoodsId(getSelectBrand(), getSelectType(), getSelectName())
            if (goodsId==-1){
                Snackbar.make(window.decorView,"请重新选择类型",Snackbar.LENGTH_SHORT).show()
            }
        }
        name_picker.setOnValueChangedListener { picker, oldVal, newVal ->
            val goodsId = DBManager.getGoodsId(getSelectBrand(), getSelectType(), getSelectName())
            if (goodsId==-1){
                Snackbar.make(window.decorView,"请重新选择名字",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBrandTypeName() {
        brands = DBManager.brands().toTypedArray()
        if (brands.isNotEmpty()) {
            brand_picker.displayedValues = brands
            brand_picker.minValue = 0
            brand_picker.maxValue = brands.size - 1
        }
        types = DBManager.types().toTypedArray()
        if (types.isNotEmpty()) {
            type_picker.displayedValues = types
            type_picker.minValue = 0
            type_picker.maxValue = types.size - 1
        }
        names = DBManager.goodsNames().toTypedArray()
        if (names.isNotEmpty()) {
            name_picker.displayedValues = names
            name_picker.minValue = 0
            name_picker.maxValue = names.size - 1
        }
    }


    override fun onResume() {
        super.onResume()
        updateBrandTypeName()
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

    private fun getPSCount(): Int {
        val count = purchase_count.text.toString()
        return if (TextUtils.isEmpty(count)) 0 else count.toInt()
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
        val psCount = getPSCount()
        val customerName = getCustomerName()
        val goodsId = DBManager.getGoodsId(selectBrand, selectType, selectName)
        DBManager.ps(PSBean(0, goodsId, inputPrice, customerName, isP,psCount))
        finish()
    }


}
