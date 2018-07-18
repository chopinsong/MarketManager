package com.chopin.marketmanager.ui.fragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.StockBean
import com.chopin.marketmanager.util.i

class StockAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    var mData = ArrayList<StockBean>()

    fun setData(data: ArrayList<StockBean>) {
        mData.clear()
        mData.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addData(b: StockBean, position: Int = -1) {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_item_layout, parent, false)
        return StockViewHolder(view)
    }

    override fun getItemCount(): Int {
        i("size=${mData.size}")
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("chopin", "onBindViewHolder")
        val h = holder as StockViewHolder
        val bean = mData[position]
        h.itemBrandTypeTv.text = String.format("%s%s", bean.goods.brand, bean.goods.type)
        h.itemCountTv.text = bean.count.toString()
    }

}

class StockViewHolder(v: View) : ViewHolder(v) {
    val itemBrandTypeTv = v.findViewById<TextView>(R.id.stock_item_brand_type_tv)
    val itemCountTv = v.findViewById<TextView>(R.id.stock_item_count_tv)
}