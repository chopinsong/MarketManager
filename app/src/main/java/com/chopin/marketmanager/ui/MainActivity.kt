package com.chopin.marketmanager.ui

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.chopin.marketmanager.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var adapter:PSAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            SelectPSDialog().show(fragmentManager,"chopin")
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        purchase_shipment_list.layoutManager = layoutManager
         adapter = PSAdapter()
        purchase_shipment_list.adapter = adapter

        updateList()

    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        object:AsyncTask<Void,Void,ArrayList<PSItemBean>>(){
            override fun doInBackground(vararg params: Void?): ArrayList<PSItemBean> {
                return DBManager.getPSBeans()
            }

            override fun onPostExecute(result: ArrayList<PSItemBean>) {
                super.onPostExecute(result)
                Log.i("chopin","result size=${result.size}")
                adapter.setData(result)
            }

        }.execute()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_purchase -> {
                // Handle the camera action
            }
            R.id.nav_shipments -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
