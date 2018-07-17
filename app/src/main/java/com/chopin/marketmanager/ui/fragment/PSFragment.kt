package com.chopin.marketmanager.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import com.chopin.marketmanager.util.getProgressDialog
import com.chopin.marketmanager.util.showAddGoods
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class PSFragment : MyDialogFragment() {
    var isP = false
    private var brands = arrayOf("")
    private var types = arrayOf("")
    private var names = arrayOf("")

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        isP = arguments.getBoolean("isP", true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, b: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.purchase_layout, container)
    }

    override fun onViewCreated(v: View, b: Bundle?) {
        updateBrands()
        commit_btn.setOnClickListener { commit() }
        if (isP) {
            add_goods_btn.setOnClickListener { showAddGoods(fragmentManager) { updateBrands() } }
            add_goods_btn.visibility = View.VISIBLE
        } else {
            add_goods_btn.visibility = View.GONE
        }
        brand_picker.setOnValueChangedListener { _, _, _ ->
            updateTypes(getSelectBrand())
        }
        type_picker.setOnValueChangedListener { _, _, _ ->
            updateNames(getSelectBrand(), getSelectType())
            checkLeftGoodsCount()
        }
        purchase_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s) && !isP) {
                    checkLeftGoodsCount()
                }
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onStart() {
        super.onStart()
        val params = dialog.window.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = params
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    private fun updateBrands() {
        async {
            brands = DBManager.brands().toTypedArray()
            uiThread {
                if (brands.isNotEmpty()) {
                    brand_picker.displayedValues = brands
                    brand_picker.minValue = 0
                    brand_picker.maxValue = brands.size - 1
                }
            }
        }
    }

    private fun updateTypes(brand: String) {
        async {
            types = DBManager.types("${GoodsTable.BRAND}=\"$brand\"").toTypedArray()
            uiThread {
                if (types.isNotEmpty()) {
                    type_picker.displayedValues = types
                    type_picker.minValue = 0
                    type_picker.maxValue = types.size - 1
                }
            }
        }
    }

    private fun updateNames(brand: String, type: String) {
        async {
            names = DBManager.goodsNames("${GoodsTable.BRAND}=\"$brand\" and ${GoodsTable.TYPE} =\"$type\"").toTypedArray()
            uiThread {
                if (names.isNotEmpty()) {
                    type_picker.displayedValues = types
                    type_picker.minValue = 0
                    type_picker.maxValue = types.size - 1
                }
            }
        }
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
                    commit_btn.enabled = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        context.toast("当前库存不足,$selectBrand$selectType${selectName}只有${goodsCountLeft}个")
                    }
                } else {
                    commit_btn.enabled = true
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
                dismiss()
            }
        }
    }


}