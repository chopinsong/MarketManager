package com.chopin.marketmanager.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import kotlinx.android.synthetic.main.goods_picker_layout.view.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread


class GoodsPicker : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        orientation = HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        gravity = Gravity.CENTER
//        LayoutInflater.from(context).inflate(R.layout.goods_picker_layout, this, true)

        updateBrands()
        goods_picker_brand.setOnValueChangedListener { _, _, _ ->
            updateTypes(getSelectBrand())
        }
        goods_picker_type.setOnValueChangedListener { _, _, _ ->
            updateNames(getSelectBrand(), getSelectType())
            checkLeftCount.invoke()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var brands = arrayOf<String>()
    private var types = arrayOf<String>()
    private var names = arrayOf<String>()
    private var checkLeftCount: () -> Unit = {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    fun checkLeftCount(func: () -> Unit) {
        checkLeftCount = func
    }

    fun updateBrands() {
        async {
            try {
                brands = DBManager.brands().toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (brands.isNotEmpty()) {
                    goods_picker_brand.refreshByNewDisplayedValues(brands)
                }
                updateTypes(getSelectBrand())
            }
        }
    }

    fun updateTypes(brand: String) {
        async {
            try {
                types = DBManager.types("${GoodsTable.BRAND}=\"$brand\"").toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (types.isNotEmpty()) {
                    goods_picker_type.refreshByNewDisplayedValues(types)
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
                    goods_picker_name.refreshByNewDisplayedValues(names)
                }
            }
        }
    }

    private fun getSelectName(): String {
        val name = names[goods_picker_name.value]
        return if (TextUtils.isEmpty(name)) "" else name.trim()
    }

    private fun getSelectBrand(): String {
        if (brands.isNotEmpty()) {
            val brand = brands[goods_picker_brand.value]
            return if (TextUtils.isEmpty(brand)) "" else brand.trim()
        }
        return ""
    }

    private fun getSelectType(): String {
        val type = types[goods_picker_type.value]
        return if (TextUtils.isEmpty(type)) "" else type.trim()
    }

    fun getSelectGoods(): Goods {
        val selectBrand = getSelectBrand()
        val selectType = getSelectType()
        val selectName = getSelectName()
        return Goods(-1, selectName, selectBrand, selectType, 0.0)
    }
}