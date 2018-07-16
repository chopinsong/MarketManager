package com.chopin.marketmanager.ui

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.recevier.InstallReceiver
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.SelectPSFragment
import com.chopin.marketmanager.util.Constant
import com.chopin.marketmanager.util.UpdateHelper
import com.chopin.marketmanager.util.showPsFragment
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

    private lateinit var installReceiver: InstallReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            SelectPSFragment().show(fragmentManager, "chopin")
        }

//        val toggle = ActionBarDrawerToggle(
//                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)


        nav_view.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        purchase_shipment_list.layoutManager = layoutManager
        adapter = PSAdapter(applicationContext)
        purchase_shipment_list.adapter = adapter

        updateList()

        updatePicker()

        main_filter_type_picker.setOnValueChangedListener { picker, oldVal, newVal ->
            filterType = newVal
            async {
                content = when (newVal) {
                    1 -> DBManager.brands().toTypedArray()
                    2 -> DBManager.types().toTypedArray()
                    3 -> arrayOf("进货", "出货")
                    else -> {
                        arrayOf("")
                    }
                }
                uiThread {
                    if (content.size > 0) {
                        val oldValues = main_filter_picker.displayedValues
                        if (oldValues != null && oldValues.size > content.size) {
                            main_filter_picker.minValue = 0
                            main_filter_picker.maxValue = content.size - 1
                            main_filter_picker.displayedValues = content
                        } else {
                            main_filter_picker.displayedValues = content
                            main_filter_picker.minValue = 0
                            main_filter_picker.maxValue = content.size - 1
                        }
                        handleFilter(main_filter_picker.value)
                    }
                }
            }
        }

        main_filter_picker.setOnValueChangedListener { _, _, newVal ->
            handleFilter(newVal)
        }
        val intentFilter = IntentFilter(Constant.INSTALL_ACTION)
        installReceiver = InstallReceiver(WeakReference(this))
        registerReceiver(installReceiver, intentFilter)

        async {
            if (UpdateHelper.check(applicationContext)) {
                UpdateHelper.showDownload(this@MainActivity) {
                    UpdateHelper.download(it) {
                        UpdateHelper.showInstall(this@MainActivity)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(installReceiver)
    }

    private fun handleFilter(newVal: Int) {
        async {
            val psBeans = DBManager.getPSBeans()
            val data = psBeans.filter {
                when (filterType) {
                    1 -> it.g.brand == content[newVal]
                    2 -> it.g.type == content[newVal]
                    3 -> newVal == 0 && it.isP || newVal == 1 && !it.isP
                    else -> true
                }
            }
            val nData = ArrayList<PSItemBean>()
            nData.addAll(data)
            uiThread {
                adapter.setData(nData)
            }
        }
    }

    private fun updatePicker() {
        val filterType = arrayOf("无过滤", "品牌", "类型", "进出货")
        main_filter_type_picker.displayedValues = filterType
        main_filter_type_picker.minValue = 0
        main_filter_type_picker.maxValue = filterType.size - 1
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        async {
            val data = DBManager.getPSBeans()
            uiThread {
                adapter.setData(data)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_purchase -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   showPsFragment(fragmentManager,true)
                }
            }
            R.id.nav_shipments -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showPsFragment(fragmentManager,false)
                }
            }
            R.id.nav_settings -> {

            }
        }
        item.isChecked = false
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
