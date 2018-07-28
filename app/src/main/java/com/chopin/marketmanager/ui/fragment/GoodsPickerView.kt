package com.chopin.marketmanager.ui.fragment

import android.os.Build
import android.text.TextUtils
import android.view.View
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import com.chopin.marketmanager.ui.NumberPickerView
import com.chopin.marketmanager.util.i
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class GoodsPickerView(root: View) {
    private val brandPicker = root.findViewById<NumberPickerView>(R.id.brand_picker)
    private val typePicker = root.findViewById<NumberPickerView>(R.id.type_picker)
    private val remarkPicker = root.findViewById<NumberPickerView>(R.id.remark_picker)
    private var brands = arrayOf<String>()
    private var types = arrayOf<String>()
    private var remarks = arrayOf<String>()

    private var listener: () -> Unit = {}
    fun setListener(listener: () -> Unit = {}) {
        this.listener = listener
    }

    init {
        brandPicker.setOnValueChangedListener { _, _, _ ->
            updateTypes(getSelectBrand())
        }
        typePicker.setOnValueChangedListener { _, _, _ ->
            updateNames(getSelectBrand(), getSelectType())
            listener.invoke()
        }
    }

    fun initValues(brand: String, type: String, remark: String) {
        val index = brands.indexOf(brand)
        brandPicker.value = if (index > -1) index else 0
        val indexT = types.indexOf(type)
        typePicker.value = if (indexT > -1) indexT else 0
        val indexN = remarks.indexOf(remark)
        remarkPicker.value = if (indexN > -1) indexN else 0
    }

    fun updateBrands() {
        async {
            try {
                brands = DBManager.brands().toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (brands.isNotEmpty()) {
                    try {
                        brandPicker.refreshByNewDisplayedValues(brands)
                    } catch (e: Exception) {
                        i(e.toString())
                    }
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
                    try {
                        typePicker.refreshByNewDisplayedValues(types)
                    } catch (e: Exception) {
                        i(e.toString())
                    }
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