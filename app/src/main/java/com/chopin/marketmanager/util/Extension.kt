package com.chopin.marketmanager.util

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DialogFragment
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


fun Activity.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
}

fun Any.toast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

fun Context.purchaseDrawable(c: Int = R.color.black2): VectorDrawableCompat? {
    VectorDrawableCompat.create(resources, R.drawable.ic_purchase, theme)?.let {
        it.setTint(getColor(c))
        return it
    }
    return null
}

fun Context.shipmentDrawable(c: Int = R.color.black2): VectorDrawableCompat? {
    VectorDrawableCompat.create(resources, R.drawable.ic_shipment, theme)?.let {
        it.setTint(getColor(c))
        return it
    }
    return null
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


fun Any.showStock(fm: FragmentManager) {
    StockFragment().show(fm, "stockFragment")
}

fun Any.showProfit(fm: FragmentManager) {
    ProfitFragment().show(fm, "ProfitFragment")
}

fun Any.getPSFragment(): PSFragment {
    return PSFragment()
}

fun Any.showAddGoods(fm: FragmentManager, f: (g: Goods) -> Unit = {}) {
    val af = AddGoodsFragment()
    af.setCommitListener(f)
    af.show(fm, "AddGoods")
}

fun Any.showEditGoodsFragment(fm: FragmentManager) {
    val ge = GoodsEditFragment()
    ge.show(fm, "showEditGoodsFragment")
}

fun Any.time2long(s: String): Long {
    val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return fm.parse(s).time
}


fun Any.time2shortTime(s: String): String {
    val fm = SimpleDateFormat("hh:mm", Locale.CHINA)
    return fm.format(time2long(s))
}

fun Any.showSettings(fm: FragmentManager) {
    SettingsFragment().show(fm, "SettingsFragment")
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

fun View.scaleDown(f: () -> Unit = {}) {
    val anim = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f)
    anim.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            f.invoke()
        }

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }
    })
    anim.duration = 600
    anim.start()
}

fun View.scaleClose(f: () -> Unit = {}) {
    val anim = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f)
    anim.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            f.invoke()
        }

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }
    })
    anim.duration = 600
    anim.start()
}

fun DialogFragment.snack(msg: String) {
    Snackbar.make(dialog.window.decorView, msg, Snackbar.LENGTH_LONG).show()
}

fun Activity.snack(msg: String) {
    Snackbar.make(window.decorView, msg, Snackbar.LENGTH_LONG).show()
}

fun Any.snack(v: View, msg: String) {
    Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show()
}

fun PSBean.toPSItemBean(): PSItemBean {
    val goods = DBManager.getGoodsInfo(goodsId)
    return PSItemBean(goods, psId, isPurchase, price.toString(), customerName, count.toString(), remark, time)
}

fun Activity.showGoodsLeft(b: PSItemBean) {
    async {
        val countLeft = DBManager.getGoodsCountLeft(b.g.id)
        uiThread {
            snack("${b.g.brand}${b.g.type}${b.g.remark}剩余${countLeft}件")
        }
    }
}

fun NumberPicker.refreshValues(content: Array<String>) {
    val oldValues = this.displayedValues
    if (oldValues != null && oldValues.size > content.size) {
        minValue = 0
        maxValue = content.size - 1
        displayedValues = content
    } else {
        displayedValues = content
        minValue = 0
        maxValue = content.size - 1
    }
}

fun Int.isPurchase(): Boolean {
    return this == 0
}

fun Int.isShipment(): Boolean {
    return this == 1
}

fun RecyclerView.defaultItemAnimation() {
    val defaultItemAnimator = DefaultItemAnimator()
    defaultItemAnimator.addDuration = 400
    defaultItemAnimator.removeDuration = 400
    this.itemAnimator = defaultItemAnimator
}

fun <T> Spinner.setValues(l: ArrayList<T>) {
    val yAdapter = ArrayAdapter<T>(context, android.R.layout.simple_list_item_1, l)
    adapter = yAdapter
}

fun <T> Context.setConfig(key: String, value: T) {
    val sp = getSharedPreferences("marketManager", MODE_PRIVATE)
    val e = sp.edit()
    when (value) {
        is Int -> e.putInt(key, value)
        is String -> e.putString(key, value)
        is Boolean -> e.putBoolean(key, value)
        is Long -> e.putLong(key, value)
        is Float -> e.putFloat(key, value)
    }
    e.apply()
}

fun <T> Context.getConfig(key: String): T {
    val sp = getSharedPreferences("marketManager", MODE_PRIVATE)
    return sp.all[key] as T
}

fun Activity.verifyStoragePermissions() {
    val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permission != PackageManager.PERMISSION_GRANTED) {
        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(this, Util.PERMISSIONS_STORAGE, Util.REQUEST_EXTERNAL_STORAGE)
    }
}

object Util {
    fun crTime(): String {
        val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }

    const val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


}