package com.chopin.marketmanager.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean

class PSAdapter : RecyclerView.Adapter<ViewHolder>() {
    var mData = ArrayList<PSItemBean>()

    fun setData(data: ArrayList<PSItemBean>) {
        mData = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ps_recycle_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean = mData[position]
        holder.itemBrandTv.text=bean.g.brand
        holder.itemTypeTv.text=bean.g.type
        holder.itemCustomerTv.text=bean.customerName
        holder.pOrSTv.text=bean.pOrS
        holder.itemPriceTv.text=bean.price
        holder.itemNameTv.text=bean.g.name
    }

}

class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val itemBrandTv=v.findViewById<TextView>(R.id.item_brand_tv)
    val itemTypeTv=v.findViewById<TextView>(R.id.item_type_tv)
    val itemNameTv=v.findViewById<TextView>(R.id.item_name_tv)
    val pOrSTv=v.findViewById<TextView>(R.id.p_or_s_tv)
    val itemPriceTv=v.findViewById<TextView>(R.id.item_price_tv)
    val itemCustomerTv=v.findViewById<TextView>(R.id.item_customer_tv)

}