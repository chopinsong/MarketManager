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

class SelectPSDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.select_ps_dialog_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        v.findViewById<Button>(R.id.select_purchase_btn).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = Intent(context, PSActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }
            dialog.dismiss()
        }
        v.findViewById<Button>(R.id.select_shipment_btn).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = Intent(context, PSActivity::class.java)
                i.putExtra("isP", false)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }
            dialog.dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }
}
