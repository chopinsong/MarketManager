package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.RecycleViewDivider
import kotlinx.android.synthetic.main.stock_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class StockFragment : MyDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.stock_layout, container)
    }

    private lateinit var stockAdapter: StockAdapter

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        initViews()
        showStockList()
    }

    private fun showStockList() {
        async {
            val stock = DBManager.stock()
            stock.sortBy { it.count }
            uiThread {
                stockAdapter.setData(stock)
            }
        }
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(dialog.context)
        stock_list.layoutManager = layoutManager
        stockAdapter = StockAdapter(dialog.context)
        stock_list.adapter = stockAdapter
        val defaultItemAnimator = DefaultItemAnimator()
        defaultItemAnimator.addDuration = 400
        defaultItemAnimator.removeDuration = 400
        stock_list.itemAnimator = defaultItemAnimator
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }
}
