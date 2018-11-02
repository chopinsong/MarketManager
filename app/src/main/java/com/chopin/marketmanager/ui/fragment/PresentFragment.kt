package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import kotlinx.android.synthetic.main.present_select_layout.*
import kotlinx.android.synthetic.main.purchase_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class PresentFragment : MyDialogFragment() {
    private lateinit var gpicker: GoodsPickerView
    private var l: (g: Goods, count: Int) -> Unit = { _, _ -> }

    fun setCommitListener(func: (g: Goods, count: Int) -> Unit) {
        this.l = func
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.present_select_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        setTouch(present_root_layout)
        present_count_picker.minValue = 1
        present_count_picker.maxValue = 99
        gpicker = GoodsPickerView(present_goods_picker)
        gpicker.updateBrands()
        present_commit_btn.setOnClickListener {
            l.invoke(gpicker.getSelectGoods(), present_count_picker.value)
            dismiss()
        }
        present_cancel_btn.setOnClickListener {
            dismiss()
        }

        gpicker.setListener {
            checkLeftGoodsCount()
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    private fun checkLeftGoodsCount() {
        val selectGoods = gpicker.getSelectGoods()
        val psCount = getPresentCount()
        doAsync {
            val goodsId = DBManager.getGoodsId(selectGoods)
            val goodsCountLeft = DBManager.getGoodsCountLeft(goodsId)
            uiThread {
                if (psCount > goodsCountLeft) {
                    commit_btn.isClickable = false
                    context?.toast("当前库存不足,${selectGoods.brand}${selectGoods.type}${selectGoods.remark}只有${goodsCountLeft}个")
                } else {
                    commit_btn.isClickable = true
                }
            }
        }
    }

    private fun getPresentCount(): Int {
        return present_count_picker.value
    }
}
