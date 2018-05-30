package com.chopin.marketmanager.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean

class PSAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    var mData = ArrayList<PSItemBean>()

    fun setData(data: ArrayList<PSItemBean>) {
        mData .clear()
        mData.addAll(data)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("chopin","onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ps_recycle_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("chopin","onBindViewHolder")
        val bean = mData[position]
        holder.img.setImageDrawable(context.getDrawable(if (bean.isP) R.drawable.menu_purchase else R.drawable.menu_shipment))
        holder.itemBrandTv.text=bean.g.brand
        holder.itemTypeTv.text=bean.g.type
        holder.itemCustomerTv.text=bean.customerName
        holder.itemPriceTv.text=bean.price
        holder.itemNameTv.text=bean.g.name
        holder.itemCountTv.text=bean.count
    }

}

class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val img=v.findViewById<ImageView>(R.id.item_is_p_img)
    val itemBrandTv=v.findViewById<TextView>(R.id.item_brand_tv)
    val itemTypeTv=v.findViewById<TextView>(R.id.item_type_tv)
    val itemNameTv=v.findViewById<TextView>(R.id.item_name_tv)
    val itemPriceTv=v.findViewById<TextView>(R.id.item_price_tv)
    val itemCustomerTv=v.findViewById<TextView>(R.id.item_customer_tv)
    val itemCountTv=v.findViewById<TextView>(R.id.item_count_tv)

}