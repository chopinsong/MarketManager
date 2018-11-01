package com.chopin.marketmanager.util


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.content.FileProvider
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.chopin.marketmanager.R
import org.jetbrains.anko.editText

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoUtil(private val df: DialogFragment) {
    //相册，拍照，取消
    private var camera: TextView? = null
    private var photo: TextView? = null
    private var back: TextView? = null
    private var dialog: AlertDialog? = null
    private val context: Context? = df.context

    init {
        if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED) {
            val view = initView()
            dialog = AlertDialog.Builder(context).setTitle("图片来源").setView(view).setIcon(R.mipmap.ic_logo).create()
            dialog?.show()
            addListener()
        } else {
            Toast.makeText(context, "请插入内存卡", Toast.LENGTH_SHORT).show()
        }
    }

    //设置点击背景
    private val backGroundColor: StateListDrawable
        get() {
            val press = ColorDrawable(-0x282829)
            val normal = ColorDrawable(-0x1)
            val drawable = StateListDrawable()
            drawable.addState(intArrayOf(android.R.attr.state_pressed), press)
            drawable.addState(intArrayOf(-android.R.attr.state_pressed), normal)
            return drawable
        }

    //拍照路径
    val photoPath: String
        get() {
            val file = File(Environment.getExternalStorageDirectory(), String.format("Android/data/%s/imgs/", context?.packageName))
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.path + File.separator + "photo.jpg"
        }


    private fun addListener() {
        back?.setOnClickListener { dialog?.dismiss() }
        camera?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            df.startActivityForResult(intent, CAMRA_SETRESULT_CODE)
            dialog?.dismiss()
        }
        photo?.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context?.let { it ->
                val photoURI = FileProvider.getUriForFile(it, "com.chopin.marketmanager.fileProvider", File(photoPath))
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                df.startActivityForResult(intent, PHOTO_SETRESULT_CODE)
                dialog?.dismiss()
            }
        }
    }

    private fun initView(): View {
        val layout = LinearLayout(context)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = params
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(-0x1)
        camera = TextView(context)
        val textViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        camera?.layoutParams = textViewParams
        camera?.setPadding(20, 20, 0, 20)
        camera?.text = "相册"
        camera?.textSize = 16f
        camera?.background = backGroundColor
        val blod1 = TextView(context)
        val blodViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
        blod1.layoutParams = blodViewParams
        blod1.setBackgroundColor(-0x282829)
        val blod2 = TextView(context)
        blod2.layoutParams = blodViewParams
        blod2.setBackgroundColor(-0x282829)
        photo = TextView(context)
        val photoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        photo?.layoutParams = photoParams
        photo?.setPadding(20, 20, 0, 20)
        photo?.text = "拍照"
        photo?.background = backGroundColor
        photo?.textSize = 16f
        back = TextView(context)
        val backParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        back?.layoutParams = backParams
        back?.gravity = Gravity.CENTER
        back?.setPadding(0, 25, 0, 25)
        back?.text = "取消"
        back?.textSize = 14f
        back?.background = backGroundColor
        layout.addView(camera)
        layout.addView(blod1)
        layout.addView(photo)
        layout.addView(blod2)
        layout.addView(back)
        return layout
    }

    //得到相册路径
    fun getCameraPath(data: Intent): String {
        val originalUri = data.data
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        //        Cursor cursor = ((Activity) context).managedQuery(originalUri, proj, null, null, null);
        if (originalUri == null) {
            return ""
        }
        val cursor = context?.contentResolver?.query(originalUri, proj, null, null, null)
        //按我个人理解 这个是获得用户选择的图片的索引值
        return if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst()
            //最后根据索引值获取图片路径
            val path = cursor.getString(column_index)
            cursor.close()
            path
        } else {
            ""
        }
    }

    companion object {
        // 创建一个以当前时间为名称的文件
        val CAMRA_SETRESULT_CODE = 0//相册返回码
        val PHOTO_SETRESULT_CODE = 1//拍照返回码

        // 拍照使用系统当前日期加以调整作为照片的名称
        private val photoFileName: String
            get() {
                val date = Date(System.currentTimeMillis())
                val dateFormat = SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.CHINA)
                return dateFormat.format(date) + ".jpg"
            }

        //file转换成BitMap
        fun readBitmapAutoSize(filePath: String): Bitmap? {
            // outWidth和outHeight是目标图片的最大宽度和高度，用作限制
            try {
                val opt = BitmapFactory.Options()
                opt.inJustDecodeBounds = true
                // 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
                BitmapFactory.decodeFile(filePath, opt)
                opt.inDither = false
                opt.inPreferredConfig = Bitmap.Config.RGB_565

                // 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
                // opt.inSampleSize = 1;
                opt.inSampleSize = computeSampleSize(opt, -1, 900 * 900)
                opt.inJustDecodeBounds = false
                return BitmapFactory.decodeFile(filePath, opt)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun computeSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
            val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels)
            var roundedSize: Int
            if (initialSize <= 8) {
                roundedSize = 1
                while (roundedSize < initialSize) {
                    roundedSize = roundedSize shl 1
                }
            } else {
                roundedSize = (initialSize + 7) / 8 * 8
            }
            return roundedSize
        }

        private fun computeInitialSampleSize(options: BitmapFactory.Options,
                                             minSideLength: Int, maxNumOfPixels: Int): Int {
            val w = options.outWidth.toDouble()
            val h = options.outHeight.toDouble()
            val lowerBound = if (maxNumOfPixels == -1) 1 else Math.ceil(Math.sqrt(w * h / maxNumOfPixels)).toInt()
            val upperBound = if (minSideLength == -1) 128 else
                Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength)).toInt()
            if (upperBound < lowerBound) {
                return lowerBound
            }
            return if (maxNumOfPixels == -1 && minSideLength == -1) {
                1
            } else if (minSideLength == -1) {
                lowerBound
            } else {
                upperBound
            }
        }

        //bitmap转换成字节流
        fun bitmaptoString(bitmap: Bitmap): String {
            // 将Bitmap转换成字符串
            val bStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream)
            val bytes = bStream.toByteArray()
//            val bb = Base64.encode(bytes, Base64.DEFAULT)
//            try {
//                return String(bb, Charset.forName("UTF-8")).replace("+", "%2B")
//            } catch (e: IOException) {
//                i("bitmaptoString ${e.toString()}")
//            }
            return Base64.encodeToString(bytes, Base64.DEFAULT)
//            return ""
        }

        fun stringToBitmap(string: String): Bitmap? {
            // 将字符串转换成Bitmap类型
            var bitmap: Bitmap? = null
            try {
                val bitmapArray = Base64.decode(string, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }

    }


}

