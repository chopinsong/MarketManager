package com.chopin.marketmanager.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
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
    private val brandPicker = NumberPickerView(context)
    private val typePicker = NumberPickerView(context)
    private val namePicker = NumberPickerView(context)
    private var checkLeftCount: () -> Unit = {}

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
            checkLeftCount.invoke()
        }
    }

    private fun updateBrands() {
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
                names = DBManager.goodsNames("${GoodsTable.BRAND}=\"$brand\" and ${GoodsTable.TYPE} =\"$type\"").toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (names.isNotEmpty()) {
                    namePicker.refreshByNewDisplayedValues(names)
                }
            }
        }
    }

    private fun getSelectName(): String {
        val name = names[namePicker.value]
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
        val type = types[typePicker.value]
        return if (TextUtils.isEmpty(type)) "" else type.trim()
    }

    fun getSelectGoods(): Goods {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        return Goods(-1, selectName, selectBrand, selectType, 0.0)
    }
}