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
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.StockBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.purchaseDrawable
import com.chopin.marketmanager.util.shipmentDrawable
import kotlinx.android.synthetic.main.stock_page_item.view.*
import org.jetbrains.anko.image

class MyStockPageAdapter(val context: Context, private val mListener: (s: StockBean) -> Unit) : RecyclerView.Adapter<MyStockPageAdapter.ViewHolder>() {
    private var mValues: ArrayList<StockBean> = ArrayList()
    private val mOnClickListener: View.OnClickListener
    var s: VectorDrawableCompat? = context.shipmentDrawable()
    var p: VectorDrawableCompat? = context.purchaseDrawable()

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as StockBean
            mListener.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_page_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mStockBrand.text = item.goods.brand
        holder.mStockType.text = item.goods.type
        holder.mImageView.image = p
        holder.mCount.text = item.count.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun updateData(stock: ArrayList<StockBean>) {
        mValues = stock
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<StockBean> {
        return mValues
    }

    fun plus(i: Int, back: (StockBean) -> Unit) {
        val stockBean = mValues[i]
        val psId = DBManager.ps(PSBean(psId = -1, goodsId = stockBean.goods.id, price = stockBean.goods.avgPrice, isPurchase = true, count = 1, customerName = ""))
        if (psId != -1L) {
            stockBean.count = stockBean.count + 1
            back.invoke(stockBean)
        }
        notifyItemChanged(i)
    }

    fun minus(i: Int, back: (StockBean) -> Unit) {
        val stockBean = mValues[i]
        if (stockBean.count > 0) {
            val psId = DBManager.ps(PSBean(psId = -1, goodsId = stockBean.goods.id, price = stockBean.goods.avgPrice, isPurchase = true, count = 1, customerName = ""))
            if (psId != -1L) {
                stockBean.count = stockBean.count - 1
                notifyItemChanged(i)
                back.invoke(stockBean)
            }
        }
        notifyItemChanged(i)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mStockBrand: TextView = mView.stock_brand
        val mStockType: TextView = mView.stock_type
        val mImageView: ImageView = mView.stock_image
        val mCount: TextView = mView.stock_count

        override fun toString(): String {
            return super.toString() + " '" + mStockType.text + "'"
        }
    }
}
