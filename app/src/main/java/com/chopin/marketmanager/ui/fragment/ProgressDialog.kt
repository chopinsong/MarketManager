package com.chopin.marketmanager.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.chopin.marketmanager.R

class ProgressDialog : MyDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.progress_dialog_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun onStart() {
        super.onStart()
        val params = dialog.window.attributes
        params.gravity = Gravity.CENTER
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = params
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }
}


