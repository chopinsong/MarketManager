package com.chopin.marketmanager.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.StockItem
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.ITHCallBack
import com.chopin.marketmanager.ui.LetterIndexer
import com.chopin.marketmanager.ui.MainActivity
import com.chopin.marketmanager.util.defaultItemAnimation
import com.chopin.marketmanager.util.snack
import com.chopin.marketmanager.util.toPY
import kotlinx.android.synthetic.main.stock_page_item_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.support.v4.view.ViewCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.ActivityCompat
import android.content.Intent
import com.chopin.marketmanager.ui.PurchaseActivity
import com.chopin.marketmanager.util.setDirectionScrollListener
import org.jetbrains.anko.act


class StockPage : Fragment() {
    private var listener: (s: StockItem) -> Unit = {}
    private var mAdapter: MyStockPageAdapter? = null
    var scrollListener: (Boolean, Boolean) -> Unit = {d,t->}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.stock_page_item_list, container, false)
        val list = view.list
        if (list is RecyclerView) {
            with(list) {
                layoutManager = LinearLayoutManager(context)
                mAdapter = MyStockPageAdapter(context) { v, sb, po ->
                    (activity as MainActivity).showPSFragment(selectGoods = sb)
//                    val intent = Intent(context, PurchaseActivity::class.java)
//                    intent.putExtra("selectGoods",sb)
//                    ActivityCompat.startActivity(activity!!.act, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            activity!!.act, v, "stock_image").toBundle())
//                    activity!!.act.overridePendingTransition(R.anim.dialog_push_in,0)
                }
                list.defaultItemAnimation()
                list.setDirectionScrollListener(scrollListener)
                adapter = mAdapter
                refreshData()
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
        val callback = ITHCallBack { i, d ->
            when (d) {
                ItemTouchHelper.LEFT -> {
                    mAdapter?.plus(i) { b, pb ->
                        snack(view, "进货成功,当前${b.goods.brand}${b.goods.type}为${b.count}个")
                        operaListener.invoke(pb)
                    }

                }
                ItemTouchHelper.RIGHT -> {
                    mAdapter?.minus(i) { b, pb ->
                        snack(view, "出货成功,当前${b.goods.brand}${b.goods.type}为${b.count}个")
                        operaListener.invoke(pb)
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(list)
        return view
    }


    override fun onDetach() {
        super.onDetach()
        listener = {}
    }

    fun refreshData() {
        doAsync {
            val stock = DBManager.stock()
            uiThread {
                mAdapter?.updateData(stock)
            }
        }
    }

    private var operaListener: (PSBean) -> Unit = {}

    fun setOperaListener(listener: (PSBean) -> Unit) {
        this.operaListener = listener
    }

    companion object {

        var sb = null
        @JvmStatic
        fun newInstance() = StockPage()
    }
}
