package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.ui.AddGoodsView
import kotlinx.android.synthetic.main.add_goods_layout.*

class AddGoodsFragment : MyDialogFragment() {

    lateinit var addGoodsView: AddGoodsView
    private var l: (g: Goods) -> Unit = {}
    private var goods: Goods? = null

    fun setCommitListener(func: (g: Goods) -> Unit) {
        this.l = func
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         arguments?.getSerializable("goods_edit_bean") ?.let {
             goods=it as Goods
         }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.add_goods_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        addGoodsView = AddGoodsView(add_goods_layout_root)
        addGoodsView.setCommitListener {
            l.invoke(it)
            dismiss()
        }
        addGoodsView.setCancelListener { dismiss() }
        goods?.let {
            initEditBean()
        }
    }

    private fun initEditBean() {
        goods?.let {
            addGoodsView.initEditBean(it)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

}
