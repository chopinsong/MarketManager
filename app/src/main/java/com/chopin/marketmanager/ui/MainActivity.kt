package com.chopin.marketmanager.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.PSAdapter
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var adapter: PSAdapter
    private var filterType = 0
    private var content = arrayOf("")
    private var brands = arrayOf("")
    private var types = arrayOf("")

    private var psData: ArrayList<PSItemBean> = arrayListOf()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verifyStoragePermissions()
        setSupportActionBar(toolbar)
        initView()
        refreshBrandTypes()
        initListener()
        val i = IntentFilter(Constant.ACTION_CLEAR_ALL_PS)
        i.addAction(Constant.ACTION_UPDATE_GOODS)
        i.addAction(Constant.ACTION_CLEAR_ALL_DATA)
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mReceiver, i)
    }

    private fun refreshBrandTypes() {
        brands = DBManager.brands().toTypedArray()
        types = DBManager.types().toTypedArray()
    }

    override fun onStart() {
        super.onStart()
        updateList()
        refreshBrandTypes()
        updatePicker()
        checkUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mReceiver)
    }


    override fun onResume() {
        super.onResume()
        updateList()
        refreshBrandTypes()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private var searchText: String = ""

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as android.support.v7.widget.SearchView
        searchView.queryHint = "在此输入过滤内容"
        searchView.setOnCloseListener {
            isGlobalFilter = false
            return@setOnCloseListener false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                isGlobalFilter = true
                newText?.let {
                    searchText = it
                }
                newText?.let {
                    handleFilter(0)
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_purchase -> {
                showPSFragment()
            }
            R.id.nav_shipments -> {
                showPSFragment(false)
            }
            R.id.stock -> {
                showStock(supportFragmentManager)
            }
            R.id.profit -> {
                showProfit(supportFragmentManager)
            }
            R.id.goods_list -> {
                showEditGoodsFragment(supportFragmentManager)
            }
            R.id.nav_settings -> {
                showSettings(supportFragmentManager)
            }
        }
        item.isChecked = false
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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

    private fun initView() {
        ActionBarDrawerToggle(this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        nav_view.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        purchase_shipment_list.layoutManager = layoutManager
        adapter = PSAdapter(applicationContext)
        purchase_shipment_list.adapter = adapter

        purchase_shipment_list.defaultItemAnimation()
        val hv = nav_view?.getHeaderView(nav_view.headerCount - 1)
        val vv = hv?.findViewById<TextView>(R.id.version_tv)
        vv?.text = UpdateHelper.getVersion(context = this).toString()
        vv?.setOnClickListener {
            checkUpdate()
            vv.scaleDown()
        }
    }

    private fun addData(b: PSBean) {
        doAsync {
            val pib = b.toPSItemBean()
            i("addData=${pib.g.brand}${pib.g.type}")
            uiThread {
                adapter.addData(b = pib)
                psData.add(pib)
                showGoodsLeft(pib)
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

    private var isGlobalFilter: Boolean = false
    var x = 0f
    var y = 0f
    var x2 = 0f
    var y2 = 0f
    var is_touch = false
    private fun initListener() {
        main_num_picker_layout.setOnTouchListener { v, event ->
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
                        showPSFragment()
                        is_touch = true
                    } else if ((x > v.display.width * 2 / 3) && ((y - y2) > 50)) {
                        showPSFragment(false)
                        is_touch = true
                    } else if (x2 - x > 50 && (Math.abs(y - y2) < 10)) {
                        showStock(supportFragmentManager)
                        is_touch = true
                    } else if (x - x2 > 50 && (Math.abs(y - y2) < 10)) {
                        showSettings(supportFragmentManager)
                        is_touch = true
                    }
                }
            }
            true
        }
        fab.setOnClickListener {
            showPSFragment()
        }
        m_filter_type_p.setOnValueChangedListener { _, _, type ->
            filterType = type
            content = getTypeContent()
            if (content.isNotEmpty()) {
                m_filter_p.refreshValues(content)
                handleFilter(m_filter_p.value)
            }
        }
        m_filter_p.setOnValueChangedListener { _, _, newVal ->
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
        ps.show(supportFragmentManager, "PSFragment")
    }

    private fun showPSFragment(isP: Boolean = true) {
        val ps = getPSFragment().setCommitListener {
            addData(it)
            refreshBrandTypes()
        }
        val bundle = Bundle()
        bundle.putBoolean("isP", isP)
        ps.arguments = bundle
        ps.show(supportFragmentManager, "PSFragment")
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
        Snackbar.make(window.decorView, "确定删除?", Snackbar.LENGTH_LONG).setAction("确定") { _ ->
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

    private fun showUndo(i: Int, b: PSItemBean) {
        Snackbar.make(window.decorView, "删除成功，是否撤消?", Snackbar.LENGTH_LONG).setAction("撤消") { _ ->
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

    private fun handleFilter(type: Int) {
        doAsync {
            val gs = searchText
            val data = psData.filter {
                if (!isGlobalFilter) {
                    when (filterType) {
                        1 -> it.g.brand == content[type]
                        2 -> it.g.type == content[type]
                        3 -> type.isPurchase() && it.isP || type.isShipment() && !it.isP
                        else -> true
                    }
                } else {
                    if (gs.isEmpty()) {
                        true
                    } else {
                        it.contains(gs)
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

    private fun checkUpdate() {
        try {
            UpdateHelper.update(this@MainActivity.toWeak())
        } catch (e: Exception) {
            i(e.toString())
        }
    }
}
