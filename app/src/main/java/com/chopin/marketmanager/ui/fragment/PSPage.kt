package com.chopin.marketmanager.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PSPage : Fragment() {
    private lateinit var adapter: PSAdapter
    private var filterType = 0
    private var content = arrayOf("")
    private var brands = arrayOf("")
    private var types = arrayOf("")
    var nav_view_margin:()->Int = {0}

    private var psData: ArrayList<PSItemBean> = arrayListOf()

    var dsListener:(Boolean, Boolean) -> Unit={d,t->}
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                when (it) {
                    Constant.ACTION_CLEAR_ALL_PS -> updateList()
                    Constant.ACTION_CLEAR_ALL_DATA -> {
                        updateList()
                        refreshBrandTypes()
                    }
                    Constant.ACTION_UPDATE_GOODS -> {
                        updateList()
                        refreshBrandTypes()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ps_page_item_list, container, false)
        val list = view.purchase_shipment_list
        context?.let {
            val layoutManager = LinearLayoutManager(it)
            list.layoutManager = layoutManager
            adapter = PSAdapter(it)
            list.adapter = adapter
            list.defaultItemAnimation()
        }
        refreshBrandTypes()
        initListener(view)
        val i = IntentFilter(Constant.ACTION_CLEAR_ALL_PS)
        i.addAction(Constant.ACTION_UPDATE_GOODS)
        i.addAction(Constant.ACTION_CLEAR_ALL_DATA)
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(mReceiver, i)
        }
        val pll = view.main_num_picker_layout.layoutParams as ViewGroup.MarginLayoutParams
        pll.setMargins(0, 0, 0, nav_view_margin.invoke())
        view.main_num_picker_layout.layoutParams=pll
        return view
    }

    override fun onStart() {
        super.onStart()
        updateList()
        refreshBrandTypes()
        updatePicker()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(mReceiver)
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun refreshBrandTypes() {
        brands = DBManager.brands().toTypedArray()
        types = DBManager.types().toTypedArray()
    }


    override fun onResume() {
        super.onResume()
        updateList()
        refreshBrandTypes()
    }


    private fun updatePicker() {
        m_filter_type_p.refreshValues(arrayOf("无过滤", "品牌", "类型", "进出货"))
    }


    private fun updateList() {
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

    var x = 0f
    var y = 0f
    var x2 = 0f
    var y2 = 0f
    var is_touch = false
    private fun initListener(view: View) {
        view.main_num_picker_layout.setOnTouchListener { v, event ->
            i("onTouchEvent ${event == null} ")
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    x = event.rawX
                    y = event.rawY
                    is_touch = false
                }
                if (event.action == MotionEvent.ACTION_MOVE && !is_touch) {
                    //当手指离开的时候
                    y2 = event.rawY
                    x2 = event.rawX
                    i("x $x width ${v.display.width} y $y y2$y2")
                    if ((x < v.display.width / 3) && ((y - y2) > 50)) {
//                        showPSFragment()
                        showPSFragment()
                        is_touch = true
                    } else if ((x > v.display.width * 2 / 3) && ((y - y2) > 50)) {
                        showPSFragment(false)
                        is_touch = true
                    } else if (x2 - x > 50 && (Math.abs(y - y2) < 10)) {
                        fragmentManager?.let {
                            showStock(it)
                        }
                        is_touch = true
                    } else if (x - x2 > 50 && (Math.abs(y - y2) < 10)) {
                        fragmentManager?.let {
                            showSettings(it)
                        }
                        is_touch = true
                    }
                }
            }
            true
        }
        view.m_filter_type_p.setOnValueChangedListener { _, _, type ->
            filterType = type
            content = getTypeContent()
            if (content.isNotEmpty()) {
                m_filter_p.refreshValues(content)
                handleFilter(m_filter_p.value)
            }
        }
        view.m_filter_p.setOnValueChangedListener { _, _, newVal ->
            handleFilter(newVal)
        }
        adapter.setOnDelListener { b, i ->
            showDelConfirm(i, b)
        }
        adapter.setEditListener { b, i ->
            showEditPSFragment(b) {
                updateData(it, i)
                refreshBrandTypes()
            }
        }
    }


    private fun showEditPSFragment(b: PSItemBean, func: (b: PSBean) -> Unit) {
        val ps = getPSFragment()
        val bundle = Bundle()
        bundle.putSerializable("editBean", b)
        ps.arguments = bundle
        ps.setUpdateListener(func)
        ps.show(fragmentManager, "PSFragment")
    }

    private fun showPsParseFragment(isP: Boolean = true) {
        val ppf = PSParseFragment()
        val b = Bundle()
        b.putStringArray("brandsArray", brands)
        b.putStringArray("typesArray", types)
        ppf.arguments = b
        ppf.show(fragmentManager, "PSParseFragment")
        ppf.setCancelListener {
            showPSFragment(isP)
        }
        ppf.setCommitListener { it ->
            val pm = PSManager()
            pm.ps(brand = it[1], type = it[2], count = it[3].toInt(), price = it[4].toDouble(), isP = it[0].toInt() == 1) { b ->
                addData(b)
                refreshBrandTypes()
            }
        }
    }

    fun showPSFragment(isP: Boolean = true) {
        val ps = getPSFragment().setCommitListener {
            addData(it)
            refreshBrandTypes()
        }
        val bundle = Bundle()
        bundle.putBoolean("isP", isP)
        ps.arguments = bundle
        ps.show(fragmentManager, "PSFragment")
    }

    private fun getTypeContent(): Array<String> {
        return when (filterType) {
            1 -> brands
            2 -> types
            3 -> arrayOf("进货", "出货")
            else -> {
                arrayOf("")
            }
        }
    }

    private fun showDelConfirm(i: Int, b: PSItemBean) {
        view?.let { it ->
            Snackbar.make(it, "确定删除?", Snackbar.LENGTH_LONG).setAction("确定") { _ ->
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
            Snackbar.make(it, "删除成功，是否撤消?", Snackbar.LENGTH_LONG).setAction("撤消") { _ ->
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

    fun handleFilter(type: Int, searchText: String? = null) {
        doAsync {
            val data = psData.filter {
                if (searchText == null) {
                    when (filterType) {
                        1 -> it.g.brand == content[type]
                        2 -> it.g.type == content[type]
                        3 -> type.isPurchase() && it.isP || type.isShipment() && !it.isP
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


    companion object {

        @JvmStatic
        fun newInstance() = PSPage()
    }
}
