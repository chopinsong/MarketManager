package com.chopin.marketmanager.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.getProgressDialog
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.add_goods_activity.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class AddGoodsFragment : MyDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.add_goods_activity, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        add_goods_commit_btn.setOnClickListener { commit() }
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

    private fun getBrand(): String {
        val str = add_goods_brand.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getType(): String {
        val str = add_goods_type.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun getName(): String {
        val str = add_goods_name.text.toString()
        return if (TextUtils.isEmpty(str)) "" else str
    }

    private fun commit() {
        val progressDialog = getProgressDialog()
        progressDialog.show(fragmentManager, "addGoodsActivity")
        val brand = getBrand()
        val type = getType()
        val name = getName()
        val avgPrice = getAvgPrice()
        async {
            val goodsId = DBManager.getGoodsId(brand, type, name)
            if (goodsId == -1) {
                DBManager.addGoods(Goods(brand = brand, type = type, name = name, avgPrice = avgPrice))
            } else {
                snack("商品重复")
            }
            uiThread {
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
