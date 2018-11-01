package com.chopin.marketmanager.ui

import android.graphics.Bitmap
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.add_goods_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddGoodsView(var root: View) {
    var brandEt: EditText = root.add_goods_brand
    var typeEt: EditText = root.add_goods_type
    var avgPriceEt: EditText = root.add_goods_avg_price
    var remarkEt: EditText = root.add_goods_remark
    var commitBtn: ImageView = root.add_goods_commit_btn
    var cancelBtn: ImageView = root.add_goods_cancel_btn
    var agbl: TextInputLayout = root.add_goods_brand_Layout
    var agtl: TextInputLayout = root.add_goods_type_Layout
    var agpl: TextInputLayout = root.add_goods_avg_price_Layout
    var agrl: TextInputLayout = root.add_goods_remark_Layout
    var goods_image: ImageView = root.goods_pic
    var goods_image_path:String=""

    init {
        commitBtn.setOnClickListener {
            commit(commitListener)
        }
        cancelBtn.setOnClickListener {
            cancelListener.invoke()
        }
        goods_image.setOnClickListener {
            goods_image_listener.invoke()
        }
    }

    private var commitListener: (g: Goods) -> Unit = {}

    fun setCommitListener(func: (g: Goods) -> Unit) {
        this.commitListener = func
    }

    private var cancelListener: () -> Unit = {}

    fun setCancelListener(cancelListener: () -> Unit) {
        this.cancelListener = cancelListener
    }

    private var goods_image_listener: () -> Unit = {}

    fun setGoods_Image_Listener(goods_image_listener: () -> Unit) {
        this.goods_image_listener = goods_image_listener
    }

    private fun getBrand(): String {
        val str = brandEt.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getType(): String {
        val str = typeEt.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getName(): String {
        val str = remarkEt.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getAvgPrice(): Double {
        val str = avgPriceEt.text.toString()
        return if (TextUtils.isEmpty(str)) 0.0 else str.toDouble()
    }

    fun commit(func: (g: Goods) -> Unit = {}) {
        val brand = getBrand()
        if (brand.isEmpty()) {
            agbl.error = "请输入品牌"
            return
        } else {
            agbl.error = null
        }
        val type = getType()
        if (type.isEmpty()) {
            agtl.error = "请输入类型"
            return
        } else {
            agtl.error = null
        }
        val name = getName()
        val avgPrice = getAvgPrice()
        doAsync {
            val goods = Goods(brand = brand, type = type, remark = name, avgPrice = avgPrice,image_path = goods_image_path)
            val goodsId = DBManager.getGoodsId(brand, type, name)
            if (isEditMode) {
                if (goodsId == editBean.id || goodsId == -1) {
                    goods.id = editBean.id
                    DBManager.updateGoods(goods)
                }
            } else {
                if (goodsId == -1) {
                    val id = DBManager.addGoods(goods)
                    goods.id = id.toInt()
                }
            }
            uiThread {
                if (!isEditMode) {
                    if (goodsId != -1) {
                        snack(root, "商品重复")
                    } else {
                        if (goods.id > -1) {
                            snack(root, "添加成功")
                            clearET()
                            func.invoke(goods)
                        }
                    }
                } else {
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

    }

    fun clearET() {
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
        goods_image.setGoodsImage(g.image_path.toBitmap().scale2(),gd(root.context))
        isEditMode = true
    }

    fun setGoodsImage(bitmap: Bitmap?) {
        bitmap?.let {
            goods_image_path=PhotoUtil.bitmaptoString(it)
            goods_image.setImageBitmap(it.scale2())
        }
    }

}