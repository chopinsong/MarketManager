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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mm.*
import kotlinx.android.synthetic.main.app_bar_main.*

const val PS_INFO = 0
const val STOCK = 1

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var fms = ArrayList<Fragment>()

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
        main_view_page.currentItem = i
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verifyStoragePermissions()
        setSupportActionBar(toolbar)
        initView()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fms.clear()
        val stockPage = StockPage.newInstance()
        stockPage.scrollListener = { isUp, t ->
            if (isUp) {
                supportActionBar?.hide()
                navigation.visibility = View.GONE
                fullScreen()
            } else {
                supportActionBar?.show()
                navigation.visibility = View.VISIBLE
                quitFull()
            }
        }
        val psPage = PSInfoPage.newInstance()
        fms.add(psPage)
        fms.add(stockPage)
        val myPagerAdapter = MyPagerAdapter(supportFragmentManager, fms)
        main_view_page.setNoScroll(true)
        main_view_page.adapter = myPagerAdapter
        main_view_page.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == STOCK) {
                    stockPage.refreshData()
//                    fullScreen()
                }
                if (p0 == PS_INFO) {
                    quitFull()
                }
            }

        })
        stockPage.setOperaListener {
            psPage.addData(it, false)
        }
    }

    override fun onStart() {
        super.onStart()
        checkUpdate()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (main_view_page != null && main_view_page.currentItem == STOCK) {
                setPage(PS_INFO)
            } else {
                moveTaskToBack(true)
            }
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
                    try {
                        val ps = fms[PS_INFO]
                        if (ps is PSInfoPage) {
                            ps.handleFilter(searchText = it)
                        }
                    } catch (e: Exception) {
                        print(e)
                    }
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


    private fun initView() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val hv = nav_view?.getHeaderView(nav_view.headerCount - 1)
        val vv = hv?.findViewById<TextView>(R.id.version_tv)
        vv?.text = UpdateHelper.getVersion(context = this).toString()
        vv?.setOnClickListener {
            checkUpdate()
            vv.scaleDown()
        }
    }


    fun showPSFragment(isP: Boolean = true, selectGoods: StockBean) {
        supportFragmentManager.showPSFragment(isP, selectGoods) {
            (fms[0] as PSInfoPage).handlerAddData(it)
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
