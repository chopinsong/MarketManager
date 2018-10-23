package com.chopin.marketmanager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chopin.marketmanager.R
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.LetterIndexer
import com.chopin.marketmanager.ui.fragment.bean.StockItem
import com.chopin.marketmanager.util.defaultItemAnimation
import com.chopin.marketmanager.util.toPY
import kotlinx.android.synthetic.main.stock_page_item_list.*
import kotlinx.android.synthetic.main.stock_page_item_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class StockPage : Fragment() {

    private var listener: (s: StockItem) -> Unit = {}
    private var mAdapter: MyStockPageAdapter? = null
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.stock_page_item_list, container, false)
        val list = view.list
        if (list is RecyclerView) {
            with(list) {
                layoutManager = LinearLayoutManager(context)
                mAdapter = MyStockPageAdapter(context) {}
                list.defaultItemAnimation()
                adapter = mAdapter
                doAsync {
                    val stock = DBManager.stock()
                    uiThread {
                        mAdapter?.updateData(stock)
                    }
                }
            }
        }
        val li = view.findViewById<LetterIndexer>(R.id.letter_index)
        li.setOnTouchLetterChangedListener(object : LetterIndexer.OnTouchLetterChangedListener {
            override fun onTouchLetterChanged(s: String, index: Int) {
                mAdapter?.let {
                    val data = it.getData()
                    for (i in 0 until data.size) {
                        val b = data[i]
                        val py = b.goods.brand.toPY()
                        if (TextUtils.equals(py.toUpperCase(), s)) {
                            // 匹配成功, 中断循环, 将列表移动到指定的位置
                            list.scrollToPosition(i)
                            break
                        }
                    }
                }
            }

            override fun onTouchActionUp(s: String) {}

        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        listener = {}
    }

    companion object {

        @JvmStatic
        fun newInstance(columnCount: Int) = StockPage().apply { }
    }
}
