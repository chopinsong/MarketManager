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
import com.chopin.marketmanager.util.getConfig
import com.chopin.marketmanager.util.setConfig
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
        setTouch(setting_root_layout)
        del_all_btn.setOnClickListener {
            Snackbar.make(dialog.window.decorView, "确定要删除？", Snackbar.LENGTH_INDEFINITE).setAction("确定") {
                doAsync {
                    DBManager.setAllPSDisable()
                    uiThread {
                        context?.let { c ->
                            LocalBroadcastManager.getInstance(c).sendBroadcast(Intent(Constant.ACTION_CLEAR_ALL_PS))
                            snack("清除成功")
                        }
                    }
                }
            }.show()
        }

        del_all_data_btn.setOnClickListener {
            Snackbar.make(dialog.window.decorView, "确定要删除？", Snackbar.LENGTH_INDEFINITE).setAction("确定") {
                doAsync {
                    DBManager.setAllPSDisable()
                    DBManager.setAllGoodsDisable()
                    uiThread {
                        context?.let {c->
                            LocalBroadcastManager.getInstance(c).sendBroadcast(Intent(Constant.ACTION_CLEAR_ALL_DATA))
                            snack("清除成功")
                        }

                    }
                }
            }.show()
        }
        context?.let {
            show_goods_remark.isChecked=it.getConfig(Constant.SHOW_GOODS_REMARK)?:false
        }
        show_goods_remark.setOnCheckedChangeListener { _, isChecked ->
            context?.setConfig(Constant.SHOW_GOODS_REMARK,isChecked)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

}
