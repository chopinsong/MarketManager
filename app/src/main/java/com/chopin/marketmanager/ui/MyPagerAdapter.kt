package com.chopin.marketmanager.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class MyPagerAdapter(fm: FragmentManager, private val mFragmentList: List<Fragment>) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}

