package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.getProgressDialog
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.add_goods_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class AddGoodsFragment : MyDialogFragment() {

    private var l: (g:Goods) -> Unit = {}

    fun setCommitListener(func: (g:Goods) -> Unit) {
        this.l = func
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.add_goods_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        add_goods_commit_btn.setOnClickListener { commit() }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }


    private fun getBrand(): String {
        val str = add_goods_brand.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getType(): String {
        val str = add_goods_type.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getName(): String {
        val str = add_goods_remark.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun commit() {
        val brand = getBrand()
        if (brand.isEmpty()) {
            snack("请输入品牌")
            return
        }
        val type = getType()
        if (type.isEmpty()) {
            snack("请输入类型")
            return
        }
        val progressDialog = getProgressDialog()
        progressDialog.show(fragmentManager, "addGoodsActivity")
        val name = getName()
        val avgPrice = getAvgPrice()
        async {
            val goods = Goods(brand = brand, type = type, remark = name, avgPrice = avgPrice)
            val goodsId = DBManager.getGoodsId(brand, type, name)
            if (goodsId == -1) {
                val id = DBManager.addGoods(goods)
                goods.id = id.toInt()
            }
            uiThread {
                if (goodsId != -1) {
                    snack("商品重复")
                } else {
                    if (goods.id > -1) {
                        snack("添加成功")
                        l.invoke(goods)
                    }
                }
                progressDialog.dismiss()
                dismiss()
            }
        }

    }

    private fun getAvgPrice(): Double {
        val str = add_goods_avg_price.text.toString()
        return if (TextUtils.isEmpty(str)) 0.0 else str.toDouble()
    }
}
