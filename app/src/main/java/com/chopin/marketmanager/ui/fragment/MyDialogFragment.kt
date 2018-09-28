package com.chopin.marketmanager.ui.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.WindowManager

abstract class MyDialogFragment : DialogFragment() {
    private var listener: (dialog: DialogInterface?) -> Unit = {}

    fun setOnDismissListener(f: (dialog: DialogInterface?) -> Unit) {
        this.listener = f
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener.invoke(dialog)
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