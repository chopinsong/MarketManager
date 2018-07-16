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
import com.chopin.marketmanager.ui.PSActivity
import com.chopin.marketmanager.ui.PSFragment
import com.chopin.marketmanager.ui.ProgressDialog
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

fun Any.toWeak(context: Context): WeakReference<Context> {
    return WeakReference(context)
}

fun Any.crTime(): String {
    val fm = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
    return fm.format(System.currentTimeMillis())
}

fun Any.showPSActivity(context: Context, isP: Boolean) {
//        context.startActivity<PSActivity>(Pair("isP",isP))
    val i = Intent(context, PSActivity::class.java)
    i.putExtra("isP", isP)
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(i)
}

fun Any.showPsFragment(fm: FragmentManager, isP: Boolean) {
    val f = PSFragment()
    val b = Bundle()
    b.putBoolean("isP", isP)
    f.arguments = b
    f.show(fm, "PSFragment")
}

fun Any.time2long(s: String): Long {
    val fm = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
    return fm.parse(s).time
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

fun DialogFragment.snack(msg:String){
    Snackbar.make(dialog.window.decorView,msg,Snackbar.LENGTH_LONG).show()
}

object Util {
    fun crTime(): String {
        val fm = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
        return fm.format(System.currentTimeMillis())
    }
}