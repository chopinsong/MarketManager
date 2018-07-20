package com.chopin.marketmanager.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.PSBean
import com.chopin.marketmanager.util.showPsFragment
import kotlinx.android.synthetic.main.select_ps_dialog_layout.*

class SelectPSFragment : MyDialogFragment() {

    private var update: (b:PSBean) -> Unit = {}

    fun setUpdateFunc(update: (b:PSBean) -> Unit): SelectPSFragment {
        this.update = update
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
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
    }

    private fun showPSActivity(isP: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPsFragment(fragmentManager, isP, update)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

}
