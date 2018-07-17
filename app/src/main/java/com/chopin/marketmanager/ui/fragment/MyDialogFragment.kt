package com.chopin.marketmanager.ui.fragment

import android.app.DialogFragment
import android.content.DialogInterface

abstract class MyDialogFragment : DialogFragment() {
    private var listener: (dialog: DialogInterface?) -> Unit = {}

    fun setOnDismissListener(f: (dialog: DialogInterface?) -> Unit) {
        this.listener = f
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener.invoke(dialog)
    }
}