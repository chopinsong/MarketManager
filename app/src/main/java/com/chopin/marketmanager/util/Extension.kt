package com.chopin.marketmanager.util

import android.app.Activity
import android.app.DialogFragment
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


fun Activity.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
}

fun Any.i(msg: String) {
    Log.i("chopin", msg)
}

fun Activity.setTransparentStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.getProgressDialog(): ProgressDialog {
    return ProgressDialog()
}

fun Fragment.getProgressDialog(): ProgressDialog {
    return ProgressDialog()
}

fun Activity.getPSFragment(): PSFragment {
    return PSFragment()
}

fun Context.toWeak(): WeakReference<Context> {
    return WeakReference(this)
}

fun Activity.toWeak(): WeakReference<Activity> {
    return WeakReference(this)
}

fun Any.crTime(): String {
    val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return fm.format(System.currentTimeMillis())
}


fun Any.showStock(fm: FragmentManager){
    StockFragment().show(fm,"stockFragment")
}

fun Any.showPsFragment(fm: FragmentManager, isP: Boolean, func: (b:PSBean) -> Unit = {}) {
    val f = PSFragment()
    f.setCommitListener(func)
    val b = Bundle()
    b.putBoolean("isP", isP)
    f.arguments = b
    f.show(fm, "PSFragment")
}

fun Any.showAddGoods(fm: FragmentManager, f: (g: Goods) -> Unit = {}) {
    val af = AddGoodsFragment()
    af.setCommitListener(f)
    af.show(fm, "AddGoods")
}

fun Any.time2long(s: String): Long {
    val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return fm.parse(s).time
}

fun Any.time2shorTime(s:String):String{
    val fm = SimpleDateFormat("hh:mm", Locale.CHINA)
    return fm.format(time2long(s))
}

fun Any.showSettings(fm: FragmentManager){
    SettingsFragment().show(fm,"SettingsFragment")
}

fun View.slideToUp() {
    val slide = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
    slide.duration = 400
    slide.fillAfter = true
    slide.isFillEnabled = true
    startAnimation(slide)
}

fun DialogFragment.snack(msg: String) {
    Snackbar.make(dialog.window.decorView, msg, Snackbar.LENGTH_LONG).show()
}

fun Activity.snack(msg: String) {
    Snackbar.make(window.decorView, msg, Snackbar.LENGTH_LONG).show()
}

fun PSBean.toPSItemBean(): PSItemBean {
    val goods = DBManager.getGoodsInfo(goodsId)
    return PSItemBean(goods,psId,isPurchase,price.toString(),customerName,count.toString(),time)
}

object Util {
    fun crTime(): String {
        val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }
}