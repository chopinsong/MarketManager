package com.chopin.marketmanager.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.StockBean
import com.chopin.marketmanager.ui.fragment.PSInfoPage
import com.chopin.marketmanager.ui.fragment.StockPage
import com.chopin.marketmanager.util.*
import com.chopin.marketmanager.util.Constant.IS_DOWNLOAD
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mm.*
import kotlinx.android.synthetic.main.app_bar_main.*

const val PS_INFO = 0
const val STOCK = 1

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var fms = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verifyStoragePermissions()
        setSupportActionBar(toolbar)
        viewInit()
    }

    override fun onStart() {
        super.onStart()
        checkUpdate()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as android.support.v7.widget.SearchView
        searchView.queryHint = "在此输入过滤内容"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    getPSInfoPage()?.handleFilter(searchText = it)
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
                setPage(STOCK)
                snack("选择商品左滑/点击商品以进货")
            }
            R.id.nav_shipments -> {
                setPage(STOCK)
                snack("选择商品右滑/点击商品以出货")
            }
            R.id.stock -> {
                showStock(supportFragmentManager)
            }
            R.id.profit -> {
                showProfit(supportFragmentManager)
            }
            R.id.goods_list -> {
                showEGF()
            }
            R.id.nav_settings -> {
                showSettings(supportFragmentManager) { action ->
                    when (action) {
                        Constant.ACTION_CLEAR_ALL_DATA -> getPSInfoPage()?.refresh()
                        Constant.ACTION_CLEAR_ALL_PS -> getPSInfoPage()?.updateList()
                    }
                }
            }
        }
        item.isChecked = false
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_ps_info -> {
                setPage(PS_INFO)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stock -> {
                setPage(STOCK)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun setPage(i: Int) {
        try {
            main_view_page.currentItem = i
        } catch (e: Exception) {
        }
    }

    private fun showTools(isShow: Boolean = true) {
        if (isShow) {
            supportActionBar?.show()
            navigation.visibility = View.VISIBLE
            quitFull()
        } else {
            supportActionBar?.hide()
            navigation.visibility = View.GONE
            fullScreen()
        }
    }


    private fun viewInit() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val hv = nav_view?.getHeaderView(nav_view.headerCount - 1)
        val vv = hv?.findViewById<TextView>(R.id.version_tv)
        vv?.text = UpdateHelper.getVersion(context = this).toString()
        vv?.setOnClickListener {
            setConfig(IS_DOWNLOAD,false)
            checkUpdate()
            vv.scaleDown()
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fms.clear()
        pageInit()
    }


    private fun pageInit() {
        val stockPage = StockPage.newInstance()
        stockPage.scrollListener = stockPageScrollListener
        val psPage = PSInfoPage.newInstance()
        fms.add(psPage)
        fms.add(stockPage)
        val myPagerAdapter = MyPagerAdapter(supportFragmentManager, fms)
        main_view_page.setNoScroll(true)
        main_view_page.adapter = myPagerAdapter
        main_view_page.addOnPageChangeListener(MyPageChangeListener(stockPage))
        stockPage.setOperaListener {
            psPage.addData(it, false)
        }
        toolbar.setOnClickListener {
            toolBarClick.invoke(psPage)
        }
        toolbar.setOnLongClickListener {
            showEGF()
            return@setOnLongClickListener true
        }
    }

    private fun showEGF() {
        showEditGoodsFragment(supportFragmentManager) {
            if (isStockPage()){
                getStockPage()?.refreshData()
            }
        }
    }

    private val stockPageScrollListener: (Boolean, Boolean) -> Unit = { isUp, _ ->
        if (isUp) {
            showTools(false)
        } else {
            showTools()
        }
    }

    private inner class MyPageChangeListener(val stockPage: StockPage) : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageSelected(p0: Int) {
            if (p0 == STOCK) {
                stockPage.refreshData()
            }
            if (p0 == PS_INFO) {
                quitFull()
            }
        }
    }

    private val toolBarClick: (PSInfoPage) -> Unit = { psPage ->
        if (isPSPage()){
            psPage.top()
        }
    }

    fun showPSFragment(isP: Boolean = true, selectGoods: StockBean) {
        supportFragmentManager.showPSFragment(isP, selectGoods) {
            getPSInfoPage()?.handlerAddData(it)
        }
    }

    fun getPSInfoPage(): PSInfoPage? {
        try {
            if (!fms.isEmpty()) {
                return fms[PS_INFO] as PSInfoPage
            }
        } catch (e: Exception) {
            try {
                main_view_page?.let {
                    return (main_view_page.adapter as MyPagerAdapter).getItem(PS_INFO) as PSInfoPage
                }
            } catch (e: Exception) {
            }
        }
        return null
    }

    private fun getStockPage(): StockPage? {
        try {
            if (!fms.isEmpty()) {
                return fms[STOCK] as StockPage
            }
        } catch (e: Exception) {
            try {
                main_view_page?.let {
                    return (main_view_page.adapter as MyPagerAdapter).getItem(STOCK) as StockPage
                }
            } catch (e: Exception) {
            }
        }
        return null
    }

    private fun isStockPage(): Boolean {
        main_view_page?.let {
           return it.currentItem == STOCK
        }
        return false
    }

    private fun isPSPage(): Boolean {
        main_view_page?.let {
           return it.currentItem == PS_INFO
        }
        return false
    }

    private fun checkUpdate() {
        try {
            UpdateHelper.update(this@MainActivity.toWeak())
        } catch (e: Exception) {
            i(e.toString())
        }
    }
}
