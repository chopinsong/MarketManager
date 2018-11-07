package com.chopin.marketmanager.ui.fragment


import android.content.Context
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
import com.chopin.marketmanager.util.*
import com.chopin.marketmanager.util.color.DominantColorCalculator
import kotlinx.android.synthetic.main.stock_page_item.view.*

class MyStockPageAdapter(val context: Context, private val mListener: (v: ImageView, s: StockBean, position: Int) -> Unit) : RecyclerView.Adapter<MyStockPageAdapter.ViewHolder>() {
    private var mValues: ArrayList<StockBean> = ArrayList()
    private var gd = context.goodsDrawable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_page_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mStockTitle.text = String.format("%s%s",item.goods.brand,item.goods.type)
        val scale = item.goods.image_path.toBitmap()?.scale2()
        holder.mImageView.setGoodsImage(scale)
        val dcc = DominantColorCalculator(scale?:context.goodsBitmap())
        holder.mBG.setBackgroundColor(dcc.colorScheme.primaryAccent)
        holder.mStockTitle.setTextColor(dcc.colorScheme.primaryText)
        holder.mCount.setTextColor(dcc.colorScheme.primaryText)
        holder.mCount.text = item.count.toString()
        holder.mView.tag = item
        holder.mImageView.setOnClickListener {
            mListener.invoke(it.stock_image, item, position)
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

    fun plus(i: Int, back: (StockBean, PSBean) -> Unit) {
        val stockBean = mValues[i]
        val psBean = PSBean(psId = -1, goodsId = stockBean.goods.id, price = stockBean.goods.avgPrice, isPurchase = true, count = 1, customerName = "")
        val psId = DBManager.ps(psBean)
        if (psId != -1L) {
            stockBean.count = stockBean.count + 1
            back.invoke(stockBean, psBean)
        }
        notifyItemChanged(i)
    }

    fun minus(i: Int, back: (StockBean, PSBean) -> Unit) {
        val stockBean = mValues[i]
        if (stockBean.count > 0) {
            val psBean = PSBean(psId = -1, goodsId = stockBean.goods.id, price = stockBean.goods.avgPrice, isPurchase = false, count = 1, customerName = "")
            val psId = DBManager.ps(psBean)
            if (psId != -1L) {
                stockBean.count = stockBean.count - 1
                notifyItemChanged(i)
                back.invoke(stockBean, psBean)
            }
        }
        notifyItemChanged(i)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mStockTitle: TextView = mView.stock_title
        val mImageView: ImageView = mView.stock_image
        val mCount: TextView = mView.stock_count
        val mBG:View=mView.stock_image_text_bg
    }
}
