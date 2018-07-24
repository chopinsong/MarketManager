package com.chopin.marketmanager.ui.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.sql.GoodsTable
import com.chopin.marketmanager.ui.AddGoodsView
import com.chopin.marketmanager.util.getProgressDialog
import com.chopin.marketmanager.util.scaleClose
import com.chopin.marketmanager.util.scaleDown
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class PSFragment : MyDialogFragment() {
    private var brands = arrayOf<String>()
    private var types = arrayOf<String>()
    private var remarks = arrayOf<String>()
    private var commitListener: (b: PSBean) -> Unit = {}
    private var updateListener: (b: PSBean) -> Unit = {}
    private var isP: Boolean? = null
    private var editBean: PSItemBean? = null
    private var isEditMode: Boolean = false

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val eb = arguments.getSerializable("editBean")
        eb?.let {
            editBean = it as PSItemBean
        }
        isEditMode = eb != null
        isP = if (isEditMode) {
            editBean?.isP
        } else {
            arguments.getBoolean("isP", true)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, b: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (isAddGoodShow) {
                    toggleAddGoods()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
        return inflater.inflate(R.layout.purchase_layout, container)
    }

    private var isAddGoodShow: Boolean = false

    private lateinit var addGoodsView: AddGoodsView

    override fun onViewCreated(v: View, b: Bundle?) {
        updateBrands()
        commit_btn.setOnClickListener { commit() }
        addGoodsView = AddGoodsView(add_goods_root)
        add_goods_btn.setOnClickListener {
            toggleAddGoods()
        }
        is_p_switch.setOnCheckedChangeListener { _, isChecked ->
            switchPS(isChecked)
        }
        isP?.let {
            is_p_switch.isChecked = it
            switchPS(it)
        }
        brand_picker.setOnValueChangedListener { _, _, _ ->
            updateTypes(getSelectBrand())
        }
        type_picker.setOnValueChangedListener { _, _, _ ->
            updateNames(getSelectBrand(), getSelectType())
            if (!is_p_switch.isChecked) {
                checkLeftGoodsCount()
            }
        }
        purchase_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s) && !is_p_switch.isChecked) {
                    checkLeftGoodsCount()
                }
            }

        })
        select_present_tv.setOnClickListener {
        }
        initEditBean()
    }

    private fun toggleAddGoods() {
        if (!isAddGoodShow) {
            add_goods_root.visibility = View.VISIBLE
            add_goods_btn.text = getString(R.string.commit_add_good)
        } else {
            add_goods_root.visibility = View.GONE
            add_goods_btn.text = getString(R.string.add_goods_text)
            addGoodsView.commit {
                updateBrands()
            }
        }
        isAddGoodShow = add_goods_root.visibility == View.VISIBLE
    }

    private fun switchPS(isChecked: Boolean) {
        add_goods_btn.visibility = if (isChecked) View.VISIBLE else View.GONE
        if (isChecked) {
            commit_btn.isEnabled = true
        }
    }

    private fun initEditBean() {
        async {
            uiThread {
                if (isEditMode) {
                    editBean?.let {
                        customer_et.setText(it.customerName)
                        price_et.setText(it.price)
                        purchase_count.setText(it.count)
                        remark_tv.setText(it.remark)
                        val index = brands.indexOf(it.g.brand)
                        brand_picker.value = if (index > -1) index else 0
                        val indexT = types.indexOf(it.g.type)
                        type_picker.value = if (indexT > -1) indexT else 0
                        val indexN = remarks.indexOf(it.g.remark)
                        name_picker.value = if (indexN > -1) indexN else 0
                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    fun setCommitListener(commitListener: (b: PSBean) -> Unit = {}): PSFragment {
        this.commitListener = commitListener
        return this
    }


    private fun updateBrands() {
        async {
            try {
                brands = DBManager.brands().toTypedArray()
            } catch (e: Exception) {
            }
            uiThread {
                if (brands.isNotEmpty()) {
                    brand_picker.refreshByNewDisplayedValues(brands)
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
                    type_picker.refreshByNewDisplayedValues(types)
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
                    name_picker.refreshByNewDisplayedValues(remarks)
                }
            }
        }
    }

    private fun getCustomerName(): String {
        val name = customer_et.text.toString()
        return if (TextUtils.isEmpty(name)) "" else name.trim()
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
        if (remarks.isEmpty()) {
            return ""
        }
        val name = remarks[name_picker.value]
        return if (TextUtils.isEmpty(name)) "" else name.trim()
    }

    private fun getSelectBrand(): String {
        if (brands.isNotEmpty()) {
            val brand = brands[brand_picker.value]
            return if (TextUtils.isEmpty(brand)) "" else brand.trim()
        }
        return ""
    }

    private fun getSelectType(): String {
        if (types.isNotEmpty()) {
            val type = types[type_picker.value]
            return if (TextUtils.isEmpty(type)) "" else type.trim()
        }
        return ""
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
        val selectBrand = getSelectBrand()
        if (selectBrand.isEmpty()) {
            snack("请选择品牌")
            return
        }
        val selectType = getSelectType()
        if (selectType.isEmpty()) {
            snack("请选择类型")
            return
        }
        val selectName = getSelectName()
        val inputPrice = getInputPrice()
        if (selectType.isEmpty()) {
            snack("请输入价格")
            return
        }
        val progress = getProgressDialog()
        progress.show(fragmentManager, "PSActivity")
        var psCount = getPSCount()
        psCount = if (psCount == 0) 1 else psCount
        var customerName = getCustomerName()
        customerName = if (customerName.isNotEmpty()) customerName else "未知"
        val remark = remark_tv.text.toString()
        val isP = is_p_switch.isChecked
        async {
            val goodsId = DBManager.getGoodsId(selectBrand, selectType, selectName)
            var line = 0
            var b: PSBean? = null
            if (isEditMode) {
                editBean?.let {
                    b = PSBean(it.psId, goodsId, inputPrice, customerName, isP, psCount, remark = remark)
                    b?.let { updateBean ->
                        line = DBManager.updatePS(updateBean)
                    }
                }
            } else {
                b = PSBean(psId = -1, goodsId = goodsId, price = inputPrice, customerName = customerName, isPurchase = isP, count = psCount, remark = remark)
                b?.let { psBean ->
                    val id = DBManager.ps(psBean)
                    psBean.psId = id.toInt()
                }
            }
            uiThread {
                progress.dismiss()
                b?.let { it1 ->
                    commitListener.invoke(it1)
                    if (isEditMode) {
                        if (line > 0) {
                            updateListener.invoke(it1)
                        }
                    }
                }
                dismiss()
            }
        }
    }

    fun setUpdateListener(func: (b: PSBean) -> Unit) {
        this.updateListener = func
    }

}