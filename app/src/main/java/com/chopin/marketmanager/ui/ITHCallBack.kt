package com.chopin.marketmanager.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ITHCallBack(private val psCallBack: (Int,Int) -> Unit) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        return -1
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        psCallBack.invoke(p0.adapterPosition,p1)
    }

}