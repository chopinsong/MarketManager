package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.image
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class PSFragment : MyDialogFragment() {
    private var commitListener: (b: PSBean) -> Unit = {}
    private var updateListener: (b: PSBean) -> Unit = {}
    private var isP: Boolean? = null
    private var editBean: PSItemBean? = null
    private var isEditMode: Boolean = false
    private lateinit var goodsPickerView: GoodsPickerView

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val eb = arguments?.getSerializable("editBean")
        eb?.let {
            editBean = it as PSItemBean
        }
        isEditMode = eb != null
        isP = if (isEditMode) {
            editBean?.isP
        } else {
            arguments?.getBoolean("isP", true)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, b: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
            }
            return@setOnKeyListener false
        }
        return inflater.inflate(R.layout.purchase_layout, container)
    }

    override fun onViewCreated(v: View, b: Bundle?) {
        setTouch(purchase_layout_root)
        img_switch_purchase.image = context?.purchaseDrawable()
        img_switch_shipment.image = context?.shipmentDrawable()
        var isShow = true
        context?.let {
            isShow = it.getConfig(Constant.SHOW_GOODS_REMARK) ?: false
        }
        goodsPickerView = GoodsPickerView(goods_picker_root, isShow)
        goodsPickerView.updateBrands()
        commit_btn.setOnClickListener { commit() }
        purchase_cancel_btn.setOnClickListener { dismiss() }
        add_goods_btn.setOnClickListener { _ ->
            fragmentManager?.let { it ->
                showAddGoods(it) {
                    goodsPickerView.updateBrands()
                }
            }
        }
        is_p_switch.setOnCheckedChangeListener { _, isChecked ->
            switchPS(isChecked)
            checkLeftGoodsCount()
        }
        isP?.let {
            is_p_switch.isChecked = it
            switchPS(it)
        }
        goodsPickerView.setListener {
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
            val pf = PresentFragment()
            pf.setCommitListener { presentGoods, presentCount ->
                setPresentGoods(presentGoods, presentCount)
            }
            pf.show(fragmentManager, "PresentFragment")
        }
        initEditBean()
    }

    private var presentGoods: Goods? = null

    private var presentCount: Int = 1

    private fun setPresentGoods(it: Goods, presentCount: Int) {
        this.presentGoods = it
        this.presentCount = presentCount
    }


    private fun switchPS(isChecked: Boolean) {
        add_goods_btn.visibility = if (isChecked) View.VISIBLE else View.GONE
        if (isChecked) {
            commit_btn.isEnabled = true
        }
        setSelectFocus(isChecked)
    }

    private fun initEditBean() {
        if (isEditMode) {
            editBean?.let {
                customer_et.setText(it.customerName)
                price_et.setText(it.price)
                purchase_count.setText(it.count)
                remark_tv.setText(it.remark)
                goodsPickerView.initValues(it.g.brand, it.g.type, it.g.remark)
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

    private fun setSelectFocus(isP: Boolean) {
        img_switch_purchase.image = context?.purchaseDrawable(if (isP) R.color.colorAccent else R.color.black2)
        img_switch_shipment.image = context?.shipmentDrawable(if (!isP) R.color.colorAccent else R.color.black2)
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

    private fun checkLeftGoodsCount() {
        val selectGoods = goodsPickerView.getSelectGoods()
        val psCount = getPSCount()
        doAsync {
            val goodsId = DBManager.getGoodsId(selectGoods)
            val goodsCountLeft = DBManager.getGoodsCountLeft(goodsId)
            uiThread {
                if (!is_p_switch.isChecked && psCount > goodsCountLeft) {
                    commit_btn.isClickable = false
                    purchase_count_Layout.error = "当前库存不足,${selectGoods.brand}${selectGoods.type}${selectGoods.remark}只有${goodsCountLeft}个"
                } else {
                    commit_btn.isClickable = true
                    purchase_count_Layout.error = null
                }
            }
        }
    }

    private fun commit() {
        val selectGoods = goodsPickerView.getSelectGoods()
        val selectBrand = selectGoods.brand
        if (selectBrand.isEmpty()) {
            snack("请选择品牌")
            return
        }
        val selectType = selectGoods.type
        if (selectType.isEmpty()) {
            snack("请选择类型")
            return
        }
        val selectName = selectGoods.remark
        val inputPrice = getInputPrice()
        val progress = getProgressDialog()
        progress.show(fragmentManager, "PSActivity")
        var psCount = getPSCount()
        psCount = if (psCount == 0) 1 else psCount
        var customerName = getCustomerName()
        customerName = if (customerName.isNotEmpty()) customerName else "未知"
        val remark = remark_tv.text.toString()
        val isP = is_p_switch.isChecked
        doAsync {
            val goodsId = DBManager.getGoodsId(selectBrand, selectType, selectName)
            var line = 0
            var b: PSBean? = null
            var presentBean: PSBean? = null
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
                presentGoods?.let {
                    val presentId = DBManager.getGoodsId(it)
                    presentBean = PSBean(psId = -1, goodsId = presentId, price = 0.0, customerName = customerName, isPurchase = false, count = presentCount, remark = "赠品")
                    presentBean?.let { pBean ->
                        val id = DBManager.ps(pBean)
                        pBean.psId = id.toInt()
                    }
                }
            }
            uiThread {
                progress.dismiss()
                b?.let { it1 ->
                    if (isEditMode) {
                        if (line > 0) {
                            updateListener.invoke(it1)
                        }
                    } else {
                        commitListener.invoke(it1)
                    }
                }
                presentBean?.let { presentB ->
                    commitListener.invoke(presentB)
                }
                dismiss()
            }
        }
    }

    fun setUpdateListener(func: (b: PSBean) -> Unit) {
        this.updateListener = func
    }

}