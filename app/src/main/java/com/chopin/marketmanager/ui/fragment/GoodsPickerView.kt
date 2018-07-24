package com.chopin.marketmanager.ui.fragment

import android.text.TextUtils
import android.view.View
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import com.chopin.marketmanager.ui.NumberPickerView
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class GoodsPickerView(root: View) {
    private val brandPicker = root.findViewById<NumberPickerView>(R.id.goods_picker_brand)
    private val typePicker = root.findViewById<NumberPickerView>(R.id.goods_picker_type)
    private val remarkPicker = root.findViewById<NumberPickerView>(R.id.goods_picker_remark)
    private var brands = arrayOf<String>()
    private var types = arrayOf<String>()
    private var remarks = arrayOf<String>()
    fun updateBrands() {
        async {
            try {
                brands = DBManager.brands().toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (brands.isNotEmpty()) {
                    brandPicker.refreshByNewDisplayedValues(brands)
                }
                updateTypes(getSelectBrand())
            }
        }
    }

    private fun updateTypes(brand: String) {
        async {
            try {
                types = DBManager.types("${GoodsTable.BRAND}=\"$brand\"").toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (types.isNotEmpty()) {
                    typePicker.refreshByNewDisplayedValues(types)
                }
            }
        }
    }

    private fun updateNames(brand: String, type: String) {
        async {
            try {
                remarks = DBManager.goodsNames("${GoodsTable.BRAND}=\"$brand\" and ${GoodsTable.TYPE} =\"$type\"").toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (remarks.isNotEmpty()) {
                    remarkPicker.refreshByNewDisplayedValues(remarks)
                }
            }
        }
    }

    private fun getSelectName(): String {
        if (remarks.isEmpty()) {
            return ""
        }
        val name = remarks[remarkPicker.value]
        return if (TextUtils.isEmpty(name)) "" else name.trim()
    }

    private fun getSelectBrand(): String {
        if (brands.isNotEmpty()) {
            val brand = brands[brandPicker.value]
            return if (TextUtils.isEmpty(brand)) "" else brand.trim()
        }
        return ""
    }

    private fun getSelectType(): String {
        if (types.isNotEmpty()) {
            val type = types[typePicker.value]
            return if (TextUtils.isEmpty(type)) "" else type.trim()
        }
        return ""
    }

    fun getSelectGoods(): Goods {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectRemark = getSelectName()
        return Goods(brand = selectBrand, type = selectType, remark = selectRemark)
    }
}