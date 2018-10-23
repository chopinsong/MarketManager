package com.chopin.marketmanager.ui.fragment


import android.content.Context
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.util.purchaseDrawable
import com.chopin.marketmanager.util.shipmentDrawable
import kotlinx.android.synthetic.main.ps_page_item.view.*
import org.jetbrains.anko.image

class MyPSPageViewAdapter(val context: Context, private val mListener: (PSItemBean) -> Unit)
    : RecyclerView.Adapter<MyPSPageViewAdapter.ViewHolder>() {
    private var mValues: ArrayList<PSItemBean> = ArrayList()
    private val mOnClickListener: View.OnClickListener
    var s: VectorDrawableCompat? = context.shipmentDrawable()
    var p: VectorDrawableCompat? = context.purchaseDrawable()

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PSItemBean
            mListener.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.ps_page_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mItemImage.image = if (item.isP) p else s
        holder.mItemBrand.text = item.g.brand
        holder.mItemType.text = item.g.type
        holder.mItemCustomer.text = item.customerName
        holder.mItemPrice.text = item.price
        holder.mItemCount.text = item.count
        holder.mItemTime.text = item.time
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size
    fun updateData(psBeans: ArrayList<PSItemBean>) {
        mValues = psBeans
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mItemImage: ImageView = mView.item_is_p_img
        val mItemBrand: TextView = mView.item_brand_tv
        val mItemType: TextView = mView.item_type_tv
        val mItemCustomer: TextView = mView.item_customer_tv
        val mItemPrice: TextView = mView.item_price_tv
        val mItemCount: TextView = mView.item_count_tv
        val mItemTime:TextView = mView.time_tv

        override fun toString(): String {
            return super.toString() + " '" + mItemPrice.text + "'"
        }
    }
}
