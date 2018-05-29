package com.chopin.marketmanager.ui

import android.app.DialogFragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.chopin.marketmanager.R
import com.chopin.marketmanager.util.Util

class SelectPSDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.select_ps_dialog_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        v.findViewById<Button>(R.id.select_purchase_btn).setOnClickListener {
            showPSActivity(true)
            dialog.dismiss()
        }
        v.findViewById<Button>(R.id.select_shipment_btn).setOnClickListener {
            showPSActivity(false)
            dialog.dismiss()
        }
    }

    private fun showPSActivity(isP:Boolean){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Util.showPSActivity(context,isP)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }
}
