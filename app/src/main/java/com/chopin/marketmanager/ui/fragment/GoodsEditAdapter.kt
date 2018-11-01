package com.chopin.marketmanager.ui.fragment

import android.content.Context
import android.support.v7.widget.CardView
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
    var mData = ArrayList<Goods>()

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
        if (h.right_menu_goods_edit != null) {
            h.right_menu_goods_edit.setOnClickListener { _ ->
                rListener.invoke(b, position)
                holder.goods_edit_swipe_layout.close()
            }
        }
        h.left_menu_goods_edit?.let {
            h.left_menu_goods_edit.setOnClickListener { _ ->
                lListener.invoke(b, position)
                holder.goods_edit_swipe_layout.close()
            }
        }
        h.goods_edit_image.setGoodsImage(b.image_path.toBitmap().scale2(),gd(context))
        h.goods_edit_brand.text = b.brand
        h.goods_edit_type.text = b.type
        h.goods_edit_remark.text = if (b.remark.isEmpty()) "无备注" else b.remark
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
    val goods_edit_brand = v.findViewById<TextView>(R.id.goods_edit_brand)
    val goods_edit_type = v.findViewById<TextView>(R.id.goods_edit_type)
    val goods_edit_remark = v.findViewById<TextView>(R.id.goods_edit_remark)
    val right_menu_goods_edit = v.findViewById<TextView>(R.id.right_menu_goods_edit)
    val left_menu_goods_edit = v.findViewById<TextView>(R.id.left_menu_goods_edit)
    val goods_edit_swipe_layout = v.findViewById<SwipeItemLayout>(R.id.goods_edit_swipe_layout)
    val goods_edit_image = v.findViewById<ImageView>(R.id.goods_edit_image)

}