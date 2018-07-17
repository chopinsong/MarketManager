package com.chopin.marketmanager.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class GoodsPicker(context: Context) : LinearLayout(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : this(context)

    private var brands = arrayOf("")
    private var types = arrayOf("")
    private var names = arrayOf("")
    private val brandPicker = NumberPicker(context)
    private val typePicker = NumberPicker(context)
    private val namePicker = NumberPicker(context)

    init {
        orientation = HORIZONTAL
        addView(brandPicker)
        addView(typePicker)
        addView(namePicker)
        updateBrands()
        brandPicker.setOnValueChangedListener { _, _, _ ->
            updateTypes(getSelectBrand())
        }
        typePicker.setOnValueChangedListener { _, _, _ ->
            updateNames(getSelectBrand(), getSelectType())
        }
    }

    fun updateBrands() {
        async {
            brands = DBManager.brands().toTypedArray()
            uiThread {
                if (brands.isNotEmpty()) {
                    brandPicker.displayedValues = brands
                    typePicker.minValue = 0
                    namePicker.maxValue = brands.size - 1
                }
            }
        }
    }

    private fun updateTypes(brand: String) {
        async {
            types = DBManager.types("${GoodsTable.BRAND}=$brand").toTypedArray()
            uiThread {
                if (types.isNotEmpty()) {
                    brandPicker.displayedValues = types
                    typePicker.minValue = 0
                    namePicker.maxValue = types.size - 1
                }
            }
        }
    }

    private fun updateNames(brand: String, type: String) {
        async {
            names = DBManager.goodsNames("${GoodsTable.BRAND}=$brand and ${GoodsTable.TYPE} =$type").toTypedArray()
            uiThread {
                if (names.isNotEmpty()) {
                    brandPicker.displayedValues = types
                    typePicker.minValue = 0
                    namePicker.maxValue = types.size - 1
                }
            }
        }
    }

    private fun getSelectName(): String {
        val name = names[namePicker.value]
        return if (TextUtils.isEmpty(name)) "" else name
    }

    private fun getSelectBrand(): String {
        if (brands.isNotEmpty()) {
            val brand = brands[brandPicker.value]
            return if (TextUtils.isEmpty(brand)) "" else brand
        }
        return ""
    }

    private fun getSelectType(): String {
        val type = types[typePicker.value]
        return if (TextUtils.isEmpty(type)) "" else type
    }

    fun getSelectGoods(): Goods {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        return Goods(-1, selectName, selectBrand, selectType, 0.0)
    }
}