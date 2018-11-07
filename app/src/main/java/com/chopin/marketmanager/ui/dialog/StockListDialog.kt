package com.chopin.marketmanager.ui.dialog

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.defaultItemAnimation
import kotlinx.android.synthetic.main.stock_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class StockListDialog : MyBaseDialog() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.stock_layout, container)
    }

    private lateinit var stockAdapter: StockAdapter

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        setTouch(stock_root_layout)
        initViews()
        showStockList()
    }

    private fun showStockList() {
        doAsync {
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
        stock_list.defaultItemAnimation()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }
}
