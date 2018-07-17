package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.settings_layout.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread

class SettingsFragment : MyDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.settings_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        del_all_btn.setOnClickListener {
           Snackbar.make(dialog.window.decorView,"确定要删除？",Snackbar.LENGTH_INDEFINITE).setAction("确定"){
               async {
                   DBManager.setAllDisable()
                    uiThread {
                        snack("清除成功")
                    }
               }
           }.show()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

}