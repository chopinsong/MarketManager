package com.chopin.marketmanager.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.sql.DBManager
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class PSActivity : AppCompatActivity() {

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
        add_goods_btn.setOnClickListener { startActivity<AddGoodsActivity>() }
        type_picker.setOnValueChangedListener { _, _, _ ->
            val goodsId = DBManager.getGoodsId(getSelectBrand(), getSelectType(), getSelectName())
            if (goodsId == -1) {
                Snackbar.make(window.decorView, "请重新选择类型", Snackbar.LENGTH_SHORT).show()
            }
        }
        name_picker.setOnValueChangedListener { _, _, _ ->
            val goodsId = DBManager.getGoodsId(getSelectBrand(), getSelectType(), getSelectName())
            if (goodsId == -1) {
                Snackbar.make(window.decorView, "请重新选择名字", Snackbar.LENGTH_SHORT).show()
            }
        }

        purchase_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    checkLeftGoodsCount()
                }
            }

        })
    }

    private fun updateBrandTypeName() {
        async {
            brands = DBManager.brands().toTypedArray()
            types = DBManager.types().toTypedArray()
            names = DBManager.goodsNames().toTypedArray()
            uiThread {
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
            }
        }
    }


    override fun onResume() {
        super.onResume()
        updateBrandTypeName()
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
        return if (TextUtils.isEmpty(name)) "" else name
    }

    private fun getSelectBrand(): String {
        if (brands.isNotEmpty()) {
            val brand = brands[brand_picker.value]
            return if (TextUtils.isEmpty(brand)) "" else brand
        }
        return ""
    }

    private fun getSelectType(): String {
        val type = types[type_picker.value]
        return if (TextUtils.isEmpty(type)) "" else type
    }

    private fun checkLeftGoodsCount() {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        val psCount = getPSCount()
        async {
            val goodsId = DBManager.getGoodsId(selectBrand, selectType, selectName)
            val goodsCountLeft = DBManager.getGoodsCountLeft(goodsId)
            uiThread {
                if (psCount > goodsCountLeft) {
                    Snackbar.make(window.decorView, "当前库存不足,只有${goodsCountLeft}个", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun commit() {
        val progress = getProgressDialog()
        progress.show(fragmentManager, "PSActivity")
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        val inputPrice = getInputPrice()
        val psCount = getPSCount()
        val customerName = getCustomerName()
        async {
            val goodsId = DBManager.getGoodsId(selectBrand, selectType, selectName)
            DBManager.ps(PSBean(0, goodsId, inputPrice, customerName, isP, psCount))
            uiThread {
                progress.dismiss()
                finish()
            }
        }
    }


}
