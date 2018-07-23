package com.chopin.marketmanager.ui.fragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.util.i
import com.chopin.marketmanager.util.time2shorTime
import swipe.SwipeItemLayout

class PSAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    var mData = ArrayList<PSItemBean>()

    fun setData(data: ArrayList<PSItemBean>) {
        mData.clear()
        i("data = ${data.size}")
        mData.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addData(position: Int = -1, b: PSItemBean) {
        if (position == -1) {
            mData.add(b)
            notifyItemChanged(mData.size - 1)
        } else {
            mData.add(position, b)
            notifyItemChanged(position)
        }
    }


    fun remove(position: Int) {
        mData.removeAt(position)
        notifyItemChanged(position)
    }

    private var listener: (b: PSItemBean, position: Int) -> Unit = { _, _ -> }

    fun setOnDelListener(listener: (b: PSItemBean, position: Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("chopin", "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ps_recycle_layout, parent, false)
        return PSViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("chopin", "onBindViewHolder")
        val h = holder as PSViewHolder
        val bean = mData[position]
        if (h.mRightMenu != null) {
            h.mRightMenu.setOnClickListener { _ ->
                listener.invoke(bean, position)
                holder.mSwipeItemLayout.close()
            }
        }
        h.img.setImageDrawable(context.getDrawable(if (bean.isP) R.mipmap.menu_purchase else R.mipmap.menu_shipment))
        h.itemBrandTv.text = bean.g.brand
        h.itemTypeTv.text = bean.g.type
        h.itemCustomerTv.text = bean.customerName
        h.itemPriceTv.text = bean.price
        h.itemRemarkTv.text = bean.remark
        h.itemCountTv.text = bean.count
        h.itemTimeTv.text = time2shorTime(bean.time)
    }

}

class PSViewHolder(v: View) : ViewHolder(v) {
    val img = v.findViewById<ImageView>(R.id.item_is_p_img)
    val itemBrandTv = v.findViewById<TextView>(R.id.item_brand_tv)
    val itemTypeTv = v.findViewById<TextView>(R.id.item_type_tv)
    val itemRemarkTv = v.findViewById<TextView>(R.id.item_remark_tv)
    val itemPriceTv = v.findViewById<TextView>(R.id.item_price_tv)
    val itemCustomerTv = v.findViewById<TextView>(R.id.item_customer_tv)
    val itemCountTv = v.findViewById<TextView>(R.id.item_count_tv)
    val mRightMenu = v.findViewById<TextView>(R.id.right_menu)
    val mSwipeItemLayout = v.findViewById<SwipeItemLayout>(R.id.swipe_layout)
    val itemTimeTv = v.findViewById<TextView>(R.id.time_tv)

}