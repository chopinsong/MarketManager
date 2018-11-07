package com.chopin.marketmanager.ui.view

import android.view.View
import android.widget.AdapterView
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.downAnim
import com.chopin.marketmanager.util.setValues
import com.chopin.marketmanager.util.upAnim
import kotlinx.android.synthetic.main.spinner_filter_layout.view.*
import org.jetbrains.anko.doAsync


class SpinnerFilterView(val root: View) {
    val typePS = arrayOf("进货", "出货")
    private var brands = ArrayList<String>()
    private var types = ArrayList<String>()
    private var crFS = -1
    var changeListener: (String, Int, String, Int) -> Unit = { _, _, _, _ -> }

    init {
        root.first_filter.setValues(arrayOf("全部", "品牌", "类型", "进出货"))
        root.first_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                crFS = position
                when (position) {
                    0 -> root.second_filter.setValues(arrayOf(""))
                    1 -> root.second_filter.setValues(brands)
                    2 -> root.second_filter.setValues(types)
                    3 -> root.second_filter.setValues(typePS)
                }
            }

        }
        root.second_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                changeListener.invoke(root.first_filter.getItemAtPosition(crFS) as String, crFS, root.second_filter.getItemAtPosition(position) as String, position)
            }

        }
    }

     fun refresh() {
        doAsync {
            brands = DBManager.brands()
            types = DBManager.types()
        }

    }

    fun setVisible(b: Boolean) {
        if (b) {
            root.filter_layout.downAnim(-root.filter_layout.height.toFloat(),0f)
        }else{
            root.filter_layout.upAnim(0f,-root.filter_layout.height.toFloat())
        }
    }
}