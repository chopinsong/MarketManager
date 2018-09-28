package com.chopin.marketmanager.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.sql.DBManager
import com.chopin.marketmanager.util.Constant
import com.chopin.marketmanager.util.snack
import kotlinx.android.synthetic.main.settings_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SettingsFragment : MyDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.settings_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        del_all_btn.setOnClickListener { _ ->
            Snackbar.make(dialog.window.decorView, "确定要删除？", Snackbar.LENGTH_INDEFINITE).setAction("确定") { _ ->
                doAsync {
                    DBManager.setAllPSDisable()
                    uiThread { _ ->
                        context?.let {
                            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(Constant.ACTION_CLEAR_ALL_PS))
                            snack("清除成功")
                        }
                    }
                }
            }.show()
        }

        del_all_data_btn.setOnClickListener { _ ->
            Snackbar.make(dialog.window.decorView, "确定要删除？", Snackbar.LENGTH_INDEFINITE).setAction("确定") { _ ->
                doAsync {
                    DBManager.setAllPSDisable()
                    DBManager.setAllGoodsDisable()
                    uiThread { _ ->
                        context?.let {
                            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(Constant.ACTION_CLEAR_ALL_DATA))
                            snack("清除成功")
                        }

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
