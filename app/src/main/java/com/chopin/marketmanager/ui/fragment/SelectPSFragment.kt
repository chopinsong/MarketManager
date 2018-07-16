package com.chopin.marketmanager.ui.fragment

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.util.showPsFragment
import kotlinx.android.synthetic.main.select_ps_dialog_layout.*

class SelectPSFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.select_ps_dialog_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        select_purchase_btn.setOnClickListener {
            showPSActivity(true)
            dialog.dismiss()
        }
        select_shipment_btn.setOnClickListener {
            showPSActivity(false)
            dialog.dismiss()
        }
        dialog.window.attributes.windowAnimations = R.style.dialogAnim
    }

    private fun showPSActivity(isP: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPsFragment(fragmentManager, isP)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onStart() {
        super.onStart()
        val params = dialog.window.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = params
    }
}
