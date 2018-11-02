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
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.util.*
import swipe.SwipeItemLayout

class GoodsEditAdapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private var mData = ArrayList<Goods>()

    fun setData(data: ArrayList<Goods>) {
        mData.clear()
        mData.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addData(b: Goods, position: Int = -1) {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goods_edit_item_layout, parent, false)
        return GoodsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private var rListener: (g: Goods, i: Int) -> Unit = { _, _ -> }
    private var lListener: (g: Goods, i: Int) -> Unit = { _, _ -> }

    fun setEditListener(l: (g: Goods, i: Int) -> Unit) {
        lListener = l
    }

    fun setDelListener(l: (g: Goods, i: Int) -> Unit) {
        rListener = l
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("chopin", "onBindViewHolder")
        val h = holder as GoodsViewHolder
        val b = mData[position]
        h.geRightMenu.setOnClickListener {
            rListener.invoke(b, position)
            holder.geSwipeLayout.close()
        }
        h.geLeftMenu.setOnClickListener {
            lListener.invoke(b, position)
            holder.geSwipeLayout.close()
        }
        h.geImage.setGoodsImage(b.image_path.toBitmap().scale2(), gd(context))
        h.geBrand.text = b.brand
        h.geType.text = b.type
        h.geRemark.text = if (b.remark.isEmpty()) "无备注" else b.remark
    }

    fun remove(i: Int) {
        mData.removeAt(i)
        notifyItemChanged(i)
    }

    fun updateData(i: Int, it: Goods) {
        mData[i] = it
        notifyItemChanged(i)
    }

}

class GoodsViewHolder(v: View) : ViewHolder(v) {
    val geBrand: TextView = v.findViewById(R.id.goods_edit_brand)
    val geType: TextView = v.findViewById(R.id.goods_edit_type)
    val geRemark: TextView = v.findViewById(R.id.goods_edit_remark)
    val geRightMenu: TextView = v.findViewById(R.id.right_menu_goods_edit)
    val geLeftMenu: TextView = v.findViewById(R.id.left_menu_goods_edit)
    val geSwipeLayout: SwipeItemLayout = v.findViewById(R.id.goods_edit_swipe_layout)
    val geImage: ImageView = v.findViewById(R.id.goods_edit_image)

}