package com.chopin.marketmanager.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.Constant
import com.chopin.marketmanager.util.defaultItemAnimation
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.goods_edit_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class GoodsEditFragment : MyDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.goods_edit_layout, container)
    }

    private lateinit var geAdapter: GoodsEditAdapter
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        setTouch(goods_edit_root_layout)
        initViews()
        showStockList()
    }

    private fun showStockList() {
        doAsync {
            val goods = DBManager.goods()
            uiThread {
                geAdapter.setData(goods)
            }
        }
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(dialog.context)
        goods_edit_list.layoutManager = layoutManager
        geAdapter = GoodsEditAdapter(dialog.context)
        goods_edit_list.adapter = geAdapter
        goods_edit_list.defaultItemAnimation()

        geAdapter.setDelListener { g, i ->
            showDelConfirm(g, i)
        }

        geAdapter.setEditListener { g, i ->
            showEditGoodsFragment(g, i)
        }


    }

    private fun showEditGoodsFragment(g: Goods, i: Int) {
        val adf = AddGoodsFragment()
        val b = Bundle()
        b.putSerializable("goods_edit_bean", g)
        adf.arguments = b
        adf.setCommitListener { it ->
            geAdapter.updateData(i, it)
            context?.let {
                LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(Constant.ACTION_UPDATE_GOODS))
            }
        }
        adf.show(fragmentManager, "EditGoodsFragment")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    private fun showDelConfirm(g: Goods, i: Int) {
        Snackbar.make(dialog.window.decorView, "确定删除?", Snackbar.LENGTH_LONG).setAction("确定") {
            doAsync {
                val line = DBManager.setGoodsEnable(g.id, false)
                uiThread {
                    if (line > 0) {
                        geAdapter.remove(i)
                    } else {
                        snack("删除失败")
                    }
                }
            }
        }.show()
    }
}
