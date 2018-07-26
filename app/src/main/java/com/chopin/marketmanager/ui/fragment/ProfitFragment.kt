package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.ProfitBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.defaultItemAnimation
import com.chopin.marketmanager.util.i
import com.chopin.marketmanager.util.setValues
import kotlinx.android.synthetic.main.profit_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class ProfitFragment : MyDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.profit_layout, container)
    }

    private lateinit var pAdapter: ProfitAdapter
    private lateinit var profits: ArrayList<ProfitBean>
    private val ymMap = HashMap<Int, ArrayList<Int>>()

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        initViews()
        initListener()
        initData()
    }

    private fun initData() {
        async {
            profits = DBManager.profits()
            for (p in profits) {
                val y = p.year
                val m = p.month
                if (!ymMap.containsKey(y)) {
                    ymMap[y] = arrayListOf(m)
                } else {
                    ymMap[y]?.let {
                        if (!it.contains(m)) {
                            it.add(m)
                            ymMap[y] = it
                        }
                    }
                }
            }
            val years = arrayListOf<Int>()
            years.addAll(ymMap.keys.sorted())
            uiThread {
                year_spinner.setValues(years)
            }
        }
    }

    private fun initListener() {
        year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ymMap[year_spinner.selectedItem]?.let {
                    month_spinner.setValues(it)
                }
            }

        }
        month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showProfitList()
            }

        }

    }

    private fun showProfitList() {
        val year = getSelectYear()
        val month = getSelectMonth()
        async {
            val ps = profits.filter {
                it.year == year && it.month == month
            }
            val m = HashMap<Goods, ProfitBean>()
            for (p in ps) {
                if (m.containsKey(p.g)) {
                    m[p.g]?.let {
                        if (p.isP){
                            it.price = it.price - p.price
                        }else{
                            it.price=it.price+p.price
                        }
                        m[p.g] = it
                    }
                } else {
                    if (p.isP){
                        p.price=0-p.price
                    }
                    m[p.g]=p
                }
            }
            val newProfits = ArrayList<ProfitBean>()
            newProfits.addAll(m.values)
            uiThread {
                pAdapter.setData(newProfits)
            }
        }
    }

    private fun getSelectYear(): Int {
        return year_spinner.selectedItem.toString().toInt()
    }

    private fun getSelectMonth(): Int {
        return month_spinner.selectedItem.toString().toInt()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(dialog.context)
        profit_list.layoutManager = layoutManager
        pAdapter = ProfitAdapter(dialog.context)
        profit_list.adapter = pAdapter
        profit_list.defaultItemAnimation()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }
}
