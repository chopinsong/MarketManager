package com.chopin.marketmanager.ui

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.PresentFragment
import com.chopin.marketmanager.util.gd
import com.chopin.marketmanager.util.scale2
import com.chopin.marketmanager.util.setGoodsImage
import com.chopin.marketmanager.util.toBitmap
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PurchaseActivity : AppCompatActivity() {
    private var commitListener: (b: PSBean) -> Unit = {}
    private var editBean: PSItemBean? = null
    private var presentGoods: Goods? = null
    private var presentCount: Int = 1
    private lateinit var selectGoods: Goods
    var isP: Boolean = true


    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.purchase_layout)
        val eb = b?.getSerializable("editBean")
        if (eb != null) {
            editBean = eb as PSItemBean
            isP = editBean?.isP ?: true
            selectGoods = editBean!!.g
        } else {
            isP = b?.getBoolean("isP", true) ?: true
//            val stockBean = b?.getSerializable("selectGoods") as StockBean
//            selectGoods = stockBean.goods
            selectGoods = DBManager.getGoodsInfo(20)
        }
        initViews()
        commit_btn.setOnClickListener { commit() }
        is_p_switch.setOnCheckedChangeListener { _, _ ->
            checkLeftGoodsCount()
        }
        purchase_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s) && !is_p_switch.isChecked) {
                    checkLeftGoodsCount()
                }
            }

        })
        select_present_tv.setOnClickListener {
            val pf = PresentFragment()
            pf.setCommitListener { presentGoods, presentCount ->
                this.presentGoods = presentGoods
                this.presentCount = presentCount
            }
            pf.show(supportFragmentManager, "PresentFragment")
        }
        ViewCompat.setTransitionName(stock_image, "stock_image")
    }

    private fun initViews() {
        is_p_switch.isChecked = isP
        if (editBean != null) {
            customer_et.setText(editBean!!.customerName)
            price_et.setText(editBean!!.price)
            purchase_count.setText(editBean!!.count)
            remark_tv.setText(editBean!!.remark)
        }
        setGoods(selectGoods)
    }

    fun setCommitListener(commitListener: (b: PSBean) -> Unit = {}): PurchaseActivity {
        this.commitListener = commitListener
        return this
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
        val intCount = if (TextUtils.isEmpty(count)) 0 else count.toInt()
        return if (intCount == 0) 1 else intCount
    }

    private fun checkLeftGoodsCount() {
        val selectGoods = selectGoods
        doAsync {
            val goodsCountLeft = DBManager.getGoodsCountLeft(selectGoods)
            uiThread {
                if (!is_p_switch.isChecked && getPSCount() > goodsCountLeft) {
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
        if (editBean != null) {
            applyUpdate()
        } else {
            applyCommit()
        }
    }

    private fun applyUpdate() {
        doAsync {
            val updateBean = PSBean(editBean!!.psId, selectGoods.id, getInputPrice(), getCustomerName(), is_p_switch.isChecked, getPSCount(), remark = remark_tv.text.toString())
            val line = DBManager.updatePS(updateBean)
            uiThread {
                if (line > 0) {
                    commitListener.invoke(updateBean)
                }
                supportFinishAfterTransition()
            }
        }
    }

    private fun applyCommit() {
        doAsync {
            val commitBean = PSBean(psId = -1, goodsId = selectGoods.id, price = getInputPrice(), customerName = getCustomerName(), isPurchase = is_p_switch.isChecked, count = getPSCount(), remark = remark_tv.text.toString())
            commitBean.psId = DBManager.ps(commitBean).toInt()
            var presentB: PSBean? = null
            presentGoods?.let {
                val presentId = DBManager.getGoodsId(it)
                val presentBean = PSBean(psId = -1, goodsId = presentId, price = 0.0, customerName = getCustomerName(), isPurchase = false, count = presentCount, remark = "赠品")
                val id = DBManager.ps(presentBean)
                presentBean.psId = id.toInt()
                presentB = presentBean
            }
            uiThread {
                commitListener.invoke(commitBean)
                presentB?.let { pb ->
                    commitListener.invoke(pb)
                }
                supportFinishAfterTransition()
            }
        }
    }


    private fun setGoods(goods: Goods) {
        stock_image.setGoodsImage(goods.image_path.toBitmap().scale2(), gd(this.applicationContext))
        stock_title.text = String.format("%s%s", goods.brand, goods.type)
    }
}
