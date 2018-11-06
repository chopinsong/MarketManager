package com.chopin.marketmanager.ui

import android.graphics.Bitmap
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.chopin.marketmanager.R.string.brand
import com.chopin.marketmanager.R.string.type
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.add_goods_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddGoodsView(private var root: View) {
    private var brandEt: EditText = root.add_goods_brand
    private var typeEt: EditText = root.add_goods_type
    private var avgPriceEt: EditText = root.add_goods_avg_price
    private var remarkEt: EditText = root.add_goods_remark
    private var commitBtn: ImageView = root.add_goods_commit_btn
    //    private var cancelBtn: ImageView = root.add_goods_cancel_btn
    private var agbl: TextInputLayout = root.add_goods_brand_Layout
    private var agtl: TextInputLayout = root.add_goods_type_Layout
    private var goodsImage: ImageView = root.goods_pic
    private var goodsImagePath: String = ""

    init {
        commitBtn.setOnClickListener {
            commit(commitListener)
        }
        goodsImage.setOnClickListener {
            goodsImageListener.invoke()
        }
    }

    var commitListener: (g: Goods) -> Unit = {}


    var goodsImageListener: () -> Unit = {}

    fun goods(): Goods {
        val brand = brandEt.text.toString()
        if (brand.isEmpty()) {
            agbl.error = "请输入品牌"
            throw IllegalArgumentException()
        } else {
            agbl.error = null
        }
        val type = typeEt.text.toString()
        if (type.isEmpty()) {
            agtl.error = "请输入类型"
            throw IllegalArgumentException()
        } else {
            agtl.error = null
        }
        val name = remarkEt.text.toString()
        val avgPriceStr = avgPriceEt.text.toString()
        val avgPrice = if (TextUtils.isEmpty(avgPriceStr)) 0.0 else avgPriceStr.toDouble()
        return Goods(brand = brand, type = type, remark = name, avgPrice = avgPrice, image_path = goodsImagePath)
    }

    fun commit(func: (g: Goods) -> Unit = {}) {
        val goods=goods()
        if (isEditMode) {
            handleUpdate(goods, func)
        } else {
            handleCommit(goods, func)
        }
    }

    private fun handleCommit(goods:Goods, func: (g: Goods) -> Unit) {
        doAsync {
            val goodsId = DBManager.getGoodsId(goods.brand, goods.type, goods.remark)
            if (goodsId == -1) {
                val id = DBManager.addGoods(goods)
                goods.id = id.toInt()
            }
            uiThread {
                if (goodsId != -1) {
                    snack(root, "商品重复")
                } else {
                    if (goods.id > -1) {
                        snack(root, "添加成功")
                        clearET()
                        func.invoke(goods)
                    }
                }
            }
        }
    }

    private fun handleUpdate(goods:Goods, func: (g: Goods) -> Unit) {
        doAsync {
            val goodsId = DBManager.getGoodsId(goods.brand, goods.type, goods.remark)
            if (goodsId == editBean.id || goodsId == -1) {
                goods.id = editBean.id
                DBManager.updateGoods(goods)
            }
            uiThread {
                if (goods.id == editBean.id) {
                    snack(root, "更新成功")
                    clearET()
                    func.invoke(goods)
                } else {
                    snack(root, "商品重复")
                }
            }
        }
    }

    private fun clearET() {
        brandEt.setText("")
        typeEt.setText("")
        avgPriceEt.setText("")
        remarkEt.setText("")
    }


    private var isEditMode = false

    private lateinit var editBean: Goods

    fun initEditBean(g: Goods) {
        editBean = g
        brandEt.setText(g.brand)
        typeEt.setText(g.type)
        avgPriceEt.setText(g.avgPrice.toString())
        remarkEt.setText(g.remark)
        goodsImage.setGoodsImage(g.image_path.toBitmap().scale2(), gd(root.context))
        isEditMode = true
    }

    fun setGoodsImage(bitmap: Bitmap?) {
        bitmap?.let {
            goodsImage.setGoodsImage(bitmap.scale2(), gd(root.context))
        }
    }

}