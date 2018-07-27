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
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.recevier.InstallReceiver
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.PSAdapter
import com.chopin.marketmanager.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var adapter: PSAdapter
    private var filterType = 0
    private var content = arrayOf("")
    private var brands = arrayOf("")
    private var types = arrayOf("")

    private lateinit var installReceiver: InstallReceiver
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
        setSupportActionBar(toolbar)
        initView()
        refreshBrandTypes()
        initListener()
        val intentFilter = IntentFilter(Constant.INSTALL_ACTION)
        installReceiver = InstallReceiver(WeakReference(this))
        registerReceiver(installReceiver, intentFilter)
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
        unregisterReceiver(installReceiver)
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
        val searchView = MenuItemCompat.getActionView(searchItem) as android.support.v7.widget.SearchView
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
                showStock(fragmentManager)
            }
            R.id.profit -> {
                showProfit(fragmentManager)
            }
            R.id.goods_list -> {
                showEditGoodsFragment(fragmentManager)
            }
            R.id.nav_settings -> {
                showSettings(fragmentManager)
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
        async {
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
    }

    private fun addData(b: PSBean) {
        async {
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
        async {
            val bean = b.toPSItemBean()
            uiThread {
                adapter.updateData(bean, i)
                psData[i] = bean
            }
        }

    }

    private var isGlobalFilter: Boolean = false

    private fun initListener() {
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
        ps.show(fragmentManager, "PSFragment")
    }

    private fun showPSFragment(isP: Boolean = true) {
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
        Snackbar.make(window.decorView, "确定删除?", Snackbar.LENGTH_LONG).setAction("确定") {
            async {
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
        Snackbar.make(window.decorView, "删除成功，是否撤消?", Snackbar.LENGTH_LONG).setAction("撤消") {
            async {
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
        async {
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
        UpdateHelper.update(this@MainActivity.toWeak())
    }
}
