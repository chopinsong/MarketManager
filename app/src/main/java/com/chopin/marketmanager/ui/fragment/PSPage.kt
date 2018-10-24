package com.chopin.marketmanager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.defaultItemAnimation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PSPage : Fragment() {
    private var listener: (PSItemBean) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ps_page_item_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                val mAdapter = MyPSPageViewAdapter(context) {}
                this.defaultItemAnimation()
                adapter = mAdapter
                doAsync {
                    val psBeans = DBManager.getPSBeans()
                    uiThread {
                        mAdapter.updateData(psBeans)
                    }
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        listener = {}
    }

    companion object {

        @JvmStatic
        fun newInstance() = PSPage()
    }
}
