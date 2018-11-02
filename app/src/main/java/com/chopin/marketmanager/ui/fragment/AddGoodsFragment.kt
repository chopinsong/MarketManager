package com.chopin.marketmanager.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.chopin.marketmanager.R
import com.chopin.marketmanager.bean.Goods
import com.chopin.marketmanager.ui.AddGoodsView
import com.chopin.marketmanager.util.PhotoUtil
import com.chopin.marketmanager.util.gd
import com.chopin.marketmanager.util.setGoodsImage
import kotlinx.android.synthetic.main.add_goods_layout.*

class AddGoodsFragment : MyDialogFragment() {

    private lateinit var addGoodsView: AddGoodsView
    private var l: (g: Goods) -> Unit = {}
    private var goods: Goods? = null
    private lateinit var photoUtil:PhotoUtil

    fun setCommitListener(func: (g: Goods) -> Unit) {
        this.l = func
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable("goods_edit_bean")?.let {
            goods = it as Goods
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.add_goods_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        setTouch(add_goods_layout_root)
        addGoodsView = AddGoodsView(add_goods_layout_root)
        addGoodsView.setCommitListener {
            l.invoke(it)
            dismiss()
        }
        addGoodsView.setCancelListener { dismiss() }
        if (goods!=null){
            initEditBean()
        }else{
            goods_pic.setGoodsImage(null,gd(context))
        }
        addGoodsView.setGoodsImageListener {
             photoUtil = PhotoUtil(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PhotoUtil.CAMRA_SETRESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                //相册选中图片路径
                data?.let {
                    val cameraPath = photoUtil.getCameraPath(it)
                    val bitmap = PhotoUtil.readBitmapAutoSize(cameraPath)
                    addGoodsView.setGoodsImage(bitmap)
                }
            }
        }
        //相机返回
        else if (PhotoUtil.PHOTO_SETRESULT_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                val photoPath = photoUtil.photoPath
                val bitmap = PhotoUtil.readBitmapAutoSize(photoPath)
                addGoodsView.setGoodsImage(bitmap)
            }
        }
    }

    private fun initEditBean() {
        goods?.let {
            addGoodsView.initEditBean(it)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
    }

}
