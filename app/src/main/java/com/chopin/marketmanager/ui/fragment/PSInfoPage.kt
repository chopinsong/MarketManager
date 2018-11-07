package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.SpinnerFilterView
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.ps_page_item_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PSInfoPage : Fragment() {
    private lateinit var adapter: PSAdapter

    private var psData: ArrayList<PSItemBean> = arrayListOf()
    private var mSpinnerFilter: SpinnerFilterView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.ps_page_item_list, container, false)
        mSpinnerFilter = SpinnerFilterView(view)
        mSpinnerFilter?.changeListener = { _, tid, v, vid ->
            handleFilter(tid, vid, v)
        }
        val list: RecyclerView = view.purchase_shipment_list
        context?.let {
            val layoutManager = LinearLayoutManager(it)
            list.layoutManager = layoutManager
            adapter = PSAdapter(it)
            list.adapter = adapter
            list.defaultItemAnimation()
        }
        mSpinnerFilter?.refresh()
        initListener()
        return view
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        updateList()
        mSpinnerFilter?.refresh()
    }

    fun updateList() {
        doAsync {
            try {
                psData = DBManager.getPSBeans()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            uiThread {
                adapter.setData(psData)
            }
        }
    }

    fun addData(b: PSBean, showLeft: Boolean = true) {
        doAsync {
            val pib = b.toPSItemBean()
            i("addData=${pib.g.brand}${pib.g.type}")
            uiThread {
                adapter.addData(b = pib)
                psData.add(pib)
                if (showLeft) {
                    showGoodsLeft(pib)
                }
            }
        }
    }

    private fun updateData(b: PSBean, i: Int) {
        doAsync {
            val bean = b.toPSItemBean()
            uiThread {
                adapter.updateData(bean, i)
                psData[i] = bean
            }
        }

    }

    private fun initListener() {
        adapter.setOnDelListener { b, i ->
            showDelConfirm(i, b)
        }
        adapter.setEditListener { b, i ->
            fragmentManager?.showEditPSFragment(b) {
                updateData(it, i)
                mSpinnerFilter?.refresh()
            }
        }
    }


    fun handlerAddData(it: PSBean) {
        addData(it)
        mSpinnerFilter?.refresh()
    }

    private fun showDelConfirm(i: Int, b: PSItemBean) {
        view?.let { it ->
            Snackbar.make(it, "确定删除?", Snackbar.LENGTH_LONG).setAction("确定") {
                doAsync {
                    val line = DBManager.setPSEnable(b.psId, false)
                    uiThread {
                        if (line > 0) {
                            adapter.remove(i)
                            psData.removeAt(i)
                            showUndo(i, b)
                        } else {
                            snack("删除失败")
                        }
                    }
                }
            }.show()
        }
    }

    private fun showUndo(i: Int, b: PSItemBean) {
        view?.let { it ->
            Snackbar.make(it, "删除成功，是否撤消?", Snackbar.LENGTH_LONG).setAction("撤消") {
                doAsync {
                    val psEnable = DBManager.setPSEnable(b.psId, true)
                    uiThread {
                        if (psEnable > 0) {
                            adapter.addData(i, b)
                            psData.add(i, b)
                            snack("撤消成功")
                        }
                    }
                }
            }.show()

        }
    }

    fun handleFilter(firstIndex: Int = -1, secondIndex: Int = -1, secondValue: String = "", searchText: String? = null) {
        doAsync {
            val data = psData.filter {
                if (searchText == null) {
                    when (firstIndex) {
                        1 -> it.g.brand == secondValue
                        2 -> it.g.type == secondValue
                        3 -> secondIndex.isPurchase() && it.isP || secondIndex.isShipment() && !it.isP
                        else -> true
                    }
                } else {
                    if (searchText.isEmpty()) {
                        true
                    } else {
                        it.contains(searchText)
                    }
                }
            }
            val nData = ArrayList<PSItemBean>()
            nData.addAll(data)
            uiThread {
                adapter.setData(nData)
            }
        }
    }

    fun top() {
        view?.purchase_shipment_list?.smoothScrollToPosition(0)
    }


    companion object {

        @JvmStatic
        fun newInstance() = PSInfoPage()
    }
}
