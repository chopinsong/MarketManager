package com.chopin.marketmanager.util

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.bean.PSItemBean
import com.chopin.marketmanager.bean.StockBean
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.ui.fragment.*
import net.sourceforge.pinyin4j.PinyinHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.image
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun String.toPY(): String {
    var convert = ""
    if (this.length < 0) {
        return ""
    }
    val word = this.toCharArray()[0]
    val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word)
    convert += if (pinyinArray != null) {
        pinyinArray[0].toCharArray()[0]
    } else {
        word
    }
    return convert
}

fun Int.px2dp(context: Context): Int {
    val density = context.resources.displayMetrics.density//得到设备的密度
    return (this / density + 0.5f).toInt()
}

fun Int.dp2px(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density + 0.5f).toInt()
}

fun Int.px2sp(context: Context): Int {
    val scaleDensity = context.resources.displayMetrics.scaledDensity//缩放密度
    return (this / scaleDensity + 0.5f).toInt()
}

fun Int.sp2px(context: Context): Int {
    val scaleDensity = context.resources.displayMetrics.scaledDensity
    return (this * scaleDensity + 0.5f).toInt()
}

fun Activity.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
}

fun Any.toast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

fun Context.purchaseDrawable(c: Int = R.color.black2): VectorDrawableCompat? {
    return getDrawable(R.drawable.ic_purchase, c)
}

fun Context.shipmentDrawable(c: Int = R.color.black2): VectorDrawableCompat? {
    return getDrawable(R.drawable.ic_shipment, c)
}

fun Context.goodsDrawable(c: Int = R.color.black2): VectorDrawableCompat? {
    return getDrawable(R.drawable.ic_goods, c)
}

fun Context.goodsBitmap(): Bitmap? {
    val bitmap: Bitmap
    val vectorDrawable = getDrawable(R.drawable.ic_goods)
    bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
}

fun Context.getDrawable(id: Int, c: Int = R.color.black2): VectorDrawableCompat? {
    VectorDrawableCompat.create(resources, id, theme)?.let {
        it.setTint(getColor(c))
        return it
    }
    return null
}

fun Any.gd(context: Context?): VectorDrawableCompat? {
    return context?.goodsDrawable()
}

fun ImageView.setGoodsImage(b: Bitmap?, gd: VectorDrawableCompat?) {
    if (b == null) {
        image = gd
    } else {
        setImageBitmap(b)
    }
}

fun ImageView.setGoodsImage(b: Bitmap?) {
    try {
        setImageBitmap(b ?: context.goodsBitmap())
    } catch (e: Exception) {
        i("setGoodsImage $e")
    }
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


fun FragmentManager.showPSFragment(isP: Boolean = true, selectGoods: StockBean, func: (PSBean) -> Unit = {}) {
    val ps = getPSFragment().setCommitListener {
        func.invoke(it)
    }
    val bundle = Bundle()
    bundle.putBoolean("isP", isP)
    bundle.putSerializable("selectGoods", selectGoods)
    ps.arguments = bundle
    ps.show(this, "PSFragment")
}

fun FragmentManager.showEditPSFragment(b: PSItemBean, func: (b: PSBean) -> Unit) {
    val ps = getPSFragment()
    val bundle = Bundle()
    bundle.putSerializable("editBean", b)
    ps.arguments = bundle
    ps.setCommitListener(func)
    ps.show(this, "PSFragment")
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
    af.commitListener = f
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

fun Fragment.snack(msg: String) {
    view?.let {
        Snackbar.make(it, msg, Snackbar.LENGTH_LONG).show()
    }
}

fun Any.snack(v: View, msg: String) {
    Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show()
}

fun PSBean.toPSItemBean(): PSItemBean {
    val goods = DBManager.getGoodsInfo(goodsId)
    return PSItemBean(goods, psId, isPurchase, price.toString(), customerName, count.toString(), remark, time)
}

fun Activity.showGoodsLeft(b: PSItemBean) {
    doAsync {
        val countLeft = DBManager.getGoodsCountLeft(b.g.id)
        uiThread {
            snack("${b.g.brand}${b.g.type}${b.g.remark}剩余${countLeft}件")
        }
    }
}

fun Fragment.showGoodsLeft(b: PSItemBean) {
    doAsync {
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
    val yAdapter = ArrayAdapter<T>(context, R.layout.item_spinner, l)
    adapter = yAdapter
}

fun <T> Spinner.setValues(array: Array<T>) {
    val al = ArrayList<T>()
    al.addAll(array)
    setValues(al)
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

fun RecyclerView.setDirectionScrollListener(func: (Boolean, Boolean) -> Unit) {
    var distance = 0
    var visible = true
    val vc = ViewConfiguration.get(context)
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (distance < -vc.scaledTouchSlop && !visible) {
                func.invoke(false, canScrollVertically(-1))
                distance = 0
                visible = true
            } else if (distance > vc.scaledTouchSlop && visible) {
                func.invoke(true, canScrollVertically(-1))
                distance = 0
                visible = false
            }
            if ((dy > 0 && visible) || (dy < 0 && !visible))//向下滑并且可见  或者  向上滑并且不可见
                distance += dy
        }

    })
}

fun View.upAnim(s: Float = height.toFloat(), e: Float = 0f, delay: Long = 0, onEnd: () -> Unit = {}) {
    val animator = ObjectAnimator.ofFloat(this, "translationY", s, e)
    animator.startDelay = delay
    animator.interpolator = DecelerateInterpolator()
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

    })
    animator.setDuration(400).start()
}

fun View.downAnim(s: Float = 0f, e: Float = height.toFloat(), delay: Long = 0, onEnd: () -> Unit = {}) {
    val animator = ObjectAnimator.ofFloat(this, "translationY", s, e)
    animator.startDelay = delay
    animator.interpolator = DecelerateInterpolator()
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

    })
    animator.setDuration(400).start()
}

fun View.transAnim(isShow: Boolean = true, onEnd: () -> Unit = {}) {
    if (isShow) upAnim(onEnd = onEnd) else downAnim(onEnd = onEnd)
}

fun String.toBitmap(): Bitmap? {
    // 将字符串转换成Bitmap类型
    return try {
        val bitmapArray = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.toStr(): String {
    return PhotoUtil.bitmaptoString(this)
}

/**
 * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片 <br></br>
 * @param w 缩放后指定的宽度
 * @param h 缩放后指定的高度
 * @return 缩放后的中间部分图片 Bitmap
 */
fun Bitmap.scale(w: Int = 800, h: Int = 600): Bitmap {
    val scaleW: Float
    val scaleH: Float
    val x: Float
    val y: Float
    val matrix = Matrix()
    i("h = $height w= $width")
    when {
        width > height -> {
            scaleW = h.toFloat() / height
            scaleH = h.toFloat() / height
            x = ((width - w * height / h) / 2).toFloat()// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0f
        }
        width < height -> {
            scaleW = w.toFloat() / width
            scaleH = w.toFloat() / width
            x = 0f
            y = ((height - h * width / w) / 2).toFloat()// 获取bitmap源文件中y做表需要偏移的像数大小
        }
        else -> {
            scaleW = w.toFloat() / width
            scaleH = w.toFloat() / width
            x = 0f
            y = 0f
        }
    }
    matrix.postScale(scaleW, scaleH)
    return Bitmap.createBitmap(this, x.toInt(), y.toInt(), (width - x).toInt(), (height - y).toInt(), matrix, true)// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
}

fun Bitmap.scale2(w: Int = 800, h: Int = 600): Bitmap {
    val resizeBmp: Bitmap
    // 获取控件的宽高
    val bitmap = this
    var width = bitmap.width
    var height = bitmap.height
    // 控件宽高比
    val viewAspectRatio = w.toFloat() / h.toFloat()
    // 图片宽高比
    val bitmapAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    // 宽高比相等，直接使用
    when {
        viewAspectRatio == bitmapAspectRatio -> resizeBmp = bitmap
// 控件宽高比大于图片宽高比，调整高度
        viewAspectRatio > bitmapAspectRatio -> {
            height = (width / viewAspectRatio).toInt()

            resizeBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resizeBmp)
            val src = Rect(0, (bitmap.height - height) / 2, bitmap.width, (bitmap.height - height) / 2 + height)
            val dst = Rect(0, 0, width, height)
            canvas.drawBitmap(bitmap, src, dst, null)
        }
// 控件宽高比小于图片宽高比，调整宽度
        else -> {
            width = (height * viewAspectRatio).toInt()
            resizeBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val canvas = Canvas(resizeBmp)
            val src = Rect((bitmap.width - width) / 2, 0, (bitmap.width - width) / 2 + width, bitmap.height)
            val dst = Rect(0, 0, width, height)
            canvas.drawBitmap(bitmap, src, dst, null)
        }
    }
    return resizeBmp
}

fun Activity.fullScreen() {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.quitFull() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

object Util {
    fun crTime(): String {
        val fm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }

    const val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


}