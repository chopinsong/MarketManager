package com.chopin.marketmanager.ui.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

abstract class MyBaseDialog : DialogFragment() {
    private var listener: (dialog: DialogInterface?) -> Unit = {}

    fun setOnDismissListener(f: (dialog: DialogInterface?) -> Unit) {
        this.listener = f
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener.invoke(dialog)

    }

    private var sx = 0f
    private var sy = 0f
    var x = 0f
    var y = 0f
    private var hasSwipeUp = false
    fun setTouch(v: View) {
        v.setOnTouchListener { _, event ->
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    sx = event.rawX
                    sy = event.rawY
                    x = event.rawX
                    y = event.rawY
                }
                if (event.action == MotionEvent.ACTION_MOVE) {
                    //当手指离开的时候
                    offset((x - event.rawX).toInt(), (y - event.rawY).toInt())
                    x = event.rawX
                    y = event.rawY
                    if (sy - event.rawY > 8) {
                        hasSwipeUp = true
                    }
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    if (hasSwipeUp && event.rawY - sy > 150 || !hasSwipeUp && event.rawY - sy > 50) {
                        dismiss()
                    }
                    hasSwipeUp = false
                }
            }
            true
        }
    }

    fun offset(x: Int, y: Int) {
        val params = dialog.window.attributes
        params.x += x
        params.y += y
        dialog.window.attributes = params
    }

    override fun onStart() {
        super.onStart()
        val params = dialog.window.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = params
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }
}