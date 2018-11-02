package com.chopin.marketmanager.ui.fragment

import android.os.Bundle
import android.view.*
import com.chopin.marketmanager.R
import com.chopin.marketmanager.util.PSParse
import kotlinx.android.synthetic.main.ps_parse_layout.*

class PSParseFragment : MyDialogFragment() {
    private var commitListener: (Array<String>) -> Unit = {}
    private var cancelListener: () -> Unit = {}

    fun setCommitListener(func: (c: Array<String>) -> Unit) {
        this.commitListener = func
    }

    fun setCancelListener(func: () -> Unit) {
        cancelListener = func
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setWindowAnimations(R.style.dialogAnim)
        return inflater.inflate(R.layout.ps_parse_layout, container)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        val ba = arguments?.getStringArray("brandsArray") ?: arrayOf("")
        val ta = arguments?.getStringArray("typesArray") ?: arrayOf("")
        setTouch(ps_parse_root)
        ps_parse_commit_btn.setOnClickListener {
            val pp = PSParse()
            val cArray = pp.parse(ps_parse_content.text.toString(), ba, ta)
            commitListener.invoke(cArray)
        }
        ps_parse_cancel_btn.setOnClickListener {
            cancelListener.invoke()
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
        params.gravity = Gravity.CENTER
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window.attributes = params
        dialog.window.setBackgroundDrawable(context?.getDrawable(R.drawable.corner))
    }
}
