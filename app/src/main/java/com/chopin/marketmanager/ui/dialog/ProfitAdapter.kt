package com.chopin.marketmanager.ui.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.ProfitBean

class ProfitAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private var mData = ArrayList<ProfitBean>()

    fun setData(data: ArrayList<ProfitBean>) {
        mData.clear()
        mData.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addData(b: ProfitBean, position: Int = -1) {
        if (position == -1) {
            mData.add(b)
            notifyItemChanged(mData.size - 1)
        } else {
            mData.add(position, b)
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("chopin", "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profit_item_layout, parent, false)
        return ProfitViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("chopin", "onBindViewHolder")
        val h = holder as ProfitViewHolder
        val bean = mData[position]
        h.itemBrandTypeTv.text = String.format("%s%s", bean.g.brand, bean.g.type)
        h.itemPriceTv.text=bean.price.toString()
    }

}

class ProfitViewHolder(v: View) : ViewHolder(v) {
    val itemBrandTypeTv: TextView = v.findViewById(R.id.profit_item_brand_type_tv)
    val itemPriceTv: TextView = v.findViewById(R.id.profit_item_price_tv)
}