package com.chopin.marketmanager.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.chopin.marketmanager.R
import com.chopin.marketmanager.ui.fragment.PSPage
import com.chopin.marketmanager.ui.fragment.StockPage
import kotlinx.android.synthetic.main.activity_mm.*

class MMActivity : AppCompatActivity() {
    val STOCK = 0
    val PS = 1

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_stock -> {
                setPage(STOCK)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_ps -> {
                setPage(PS)
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
        setContentView(R.layout.activity_mm)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fms = ArrayList<Fragment>()
        fms.add(StockPage())
        fms.add(PSPage())
        val myPagerAdapter = MyPagerAdapter(supportFragmentManager, fms)
        main_view_page.adapter=myPagerAdapter
    }

}
