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
import com.chopin.marketmanager.util.*
import swipe.SwipeItemLayout


class PSAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private var mData = ArrayList<PSItemBean>()
    fun setData(data: ArrayList<PSItemBean>) {
        mData.clear()
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
    private var editListener: (b: PSItemBean, position: Int) -> Unit = { _, _ -> }
    fun setOnDelListener(listener: (b: PSItemBean, position: Int) -> Unit) {
        this.listener = listener
    }

    fun setEditListener(listener: (b: PSItemBean, position: Int) -> Unit) {
        this.editListener = listener
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
        h.mRightMenu.setOnClickListener {
            listener.invoke(bean, position)
            holder.mSwipeItemLayout.close()
        }
        h.mLeftMenu.let {
            h.mLeftMenu.setOnClickListener {
                editListener.invoke(bean, position)
                holder.mSwipeItemLayout.close()
            }
        }
        h.img.setGoodsImage(bean.g.image_path.toBitmap()?.scale2())
        h.itemBrandTv.text = bean.g.brand
        h.itemTypeTv.text = bean.g.type
        if (bean.customerName.isEmpty()) {
            h.itemCustomerTv.visibility = View.GONE
        } else {
            h.itemCustomerTv.text = bean.customerName
        }
        h.itemPriceTv.text = String.format("%s%s", (if (bean.isP) "-" else "+"), bean.price)
//        h.itemRemarkTv.text = if (bean.remark.isEmpty()) "无备注" else bean.remark
        h.itemCountTv.text = bean.count
        h.itemTimeTv.text = time2shortTime(bean.time)
    }

    fun updateData(b: PSItemBean, i: Int) {
        mData[i] = b
        notifyItemChanged(i)
    }

}

class PSViewHolder(v: View) : ViewHolder(v) {
    val img: ImageView = v.findViewById(R.id.item_is_p_img)
    val itemBrandTv: TextView = v.findViewById(R.id.item_brand_tv)
    val itemTypeTv: TextView = v.findViewById(R.id.item_type_tv)
    //    val itemRemarkTv = v.findViewById<TextView>(R.id.item_remark_tv)
    val itemPriceTv: TextView = v.findViewById(R.id.item_price_tv)
    val itemCustomerTv: TextView = v.findViewById(R.id.item_customer_tv)
    val itemCountTv: TextView = v.findViewById(R.id.item_count_tv)
    val mRightMenu: TextView = v.findViewById(R.id.right_menu)
    val mLeftMenu: TextView = v.findViewById(R.id.left_menu)
    val mSwipeItemLayout: SwipeItemLayout = v.findViewById(R.id.swipe_layout)
    val itemTimeTv: TextView = v.findViewById(R.id.time_tv)

}