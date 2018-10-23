package com.chopin.marketmanager.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.chopin.marketmanager.R

class LetterIndexer : View {

    private var mContext: Context? = null

    // 向右偏移多少画字符， default 30
    private var mWidthOffset = 30.0f

    // 最小字体大小
    private var mMinFontSize = 24

    // 最大字体大小
    private var mMaxFontSize = 48

    // 提示字体大小
    private var mTipFontSize = 52

    // 提示字符的额外偏移
    private var mAdditionalTipOffset = 20.0f

    // 贝塞尔曲线控制的高度
    private var mMaxBezierHeight = 150.0f

    // 贝塞尔曲线单侧宽度
    private var mMaxBezierWidth = 240.0f

    // 贝塞尔曲线单侧模拟线量
    private var mMaxBezierLines = 32

    // 列表字符颜色
    private var mFontColor = -0x1  //白色

    // 提示字符颜色
    //	int  mTipFontColor = 0xff3399ff;
    internal var mTipFontColor = -0x2cc1b8 //金


    private var mListener: OnTouchLetterChangedListener? = null

    private var constChar = arrayOf("#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    private var mConLength = 0

    private var mChooseIndex = -1
    private val mPaint = Paint()
    private val mTouch = PointF()

    private var mBezier1: Array<PointF> = Array(mMaxBezierLines) { PointF() }
    private var mBezier2: Array<PointF> = Array(mMaxBezierLines) { PointF() }

    private var mLastOffset: FloatArray? = null // 记录每一个字母的x方向偏移量, 数字<=0

    private var mScroller: Scroller? = null
    //正在动画
    private var mAnimating = false
    //动画的偏移量
    private var mAnimationOffset: Float = 0.toFloat()

    //动画隐藏
    private var mHideAnimation = false

    //手指是否抬起
    private var isUp = false

    private var mAlpha = 255

    /**
     * 控制距离顶部的距离、底部距离
     */
    private var mPaddingTop = 0
    private var mPaddingBottom = 0

    class MyHandler(private val func: () -> Unit) : Handler() {

        override fun handleMessage(msg: Message?) {
            if (msg?.what == 1) {
                func.invoke()
                return
            }
            super.handleMessage(msg)
        }
    }

    private var mHideWaitingHandler: Handler = MyHandler {
        //				mScroller.startScroll(0, 0, 255, 0, 1000);
        mHideAnimation = true
        mAnimating = false                                                        //动画mAnimating=false onDraw触发
        this@LetterIndexer.invalidate()
    }

    interface OnTouchLetterChangedListener {
        fun onTouchLetterChanged(s: String, index: Int)

        fun onTouchActionUp(s: String)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initData(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData(context, attrs)
    }

    constructor(context: Context) : super(context) {
        initData(null, null)
    }

    private fun initData(context: Context?, attrs: AttributeSet?) {
        if (context != null && attrs != null) {

            val a = context.obtainStyledAttributes(attrs, R.styleable.LetterIndexer, 0, 0)

            mWidthOffset = a.getDimension(R.styleable.LetterIndexer_widthOffset, mWidthOffset)
            mMinFontSize = a.getInteger(R.styleable.LetterIndexer_minFontSize, mMinFontSize)
            mMaxFontSize = a.getInteger(R.styleable.LetterIndexer_maxFontSize, mMaxFontSize)
            mTipFontSize = a.getInteger(R.styleable.LetterIndexer_tipFontSize, mTipFontSize)
            mMaxBezierHeight = a.getDimension(R.styleable.LetterIndexer_maxBezierHeight, mMaxBezierHeight)
            mMaxBezierWidth = a.getDimension(R.styleable.LetterIndexer_maxBezierWidth, mMaxBezierWidth)
            mMaxBezierLines = a.getInteger(R.styleable.LetterIndexer_maxBezierLines, mMaxBezierLines)
            mAdditionalTipOffset = a.getDimension(R.styleable.LetterIndexer_additionalTipOffset, mAdditionalTipOffset)
            mFontColor = a.getColor(R.styleable.LetterIndexer_fontColor, mFontColor)
            mTipFontColor = a.getColor(R.styleable.LetterIndexer_tipFontColor, mTipFontColor)
            a.recycle()
        }


        this.mContext = context
        mScroller = Scroller(getContext())
        mTouch.x = 0f
        mTouch.y = -10 * mMaxBezierWidth

//        mBezier1 = Array(mMaxBezierLines) { PointF()}
//        mBezier2 = Array(mMaxBezierLines) { PointF()}

        commonData(0, 0)
    }

    /**
     * 需 注意的是，传值单位是sp
     *
     * @param top    距离顶部的距离
     * @param bottom 距离底部的距离
     */
    private fun commonData(top: Int, bottom: Int) {
        mPaddingTop = top
        mPaddingBottom = bottom
        mConLength = constChar.size
        mLastOffset = FloatArray(mConLength)
        calculateBezierPoints()
    }

    fun setConstChar(constChar: Array<String>, top: Int, bottom: Int) {
        this.constChar = constChar
        commonData(top, bottom)
    }

    override fun onDraw(canvas: Canvas) {

        // 控件宽高
        val height = height - mPaddingTop - mPaddingBottom
        val width = width

        // 单个字母高度
        val singleHeight = height / constChar.size.toFloat()

        var workHeight = mPaddingTop

        if (mAlpha == 0)
            return

        //恢复画笔的默认设置。
        mPaint.reset()

        /**
         * 遍历所以字母内容
         */
        for (i in 0 until mConLength) {

            mPaint.color = mFontColor
            mPaint.isAntiAlias = true

            val xPos = width - mWidthOffset           // 字母在 x 轴的位置      基本保持不变
            val yPos = workHeight + singleHeight / 2  //字母在 y 轴的位置     该值一直在变化

            // 根据当前字母y的位置计算得到字体大小
            val fontSize = adjustFontSize(i, yPos)
            mPaint.textSize = fontSize.toFloat()
            mAlpha = 255 - fontSize * 4
            mPaint.alpha = mAlpha
            if (i == mChooseIndex) {
                mPaint.color = Color.parseColor("#F50527")
            }

            // 添加一个字母的高度
            workHeight += singleHeight.toInt()
            // 绘制字母
            drawTextInCenter(canvas, constChar[i], xPos + ajustXPosAnimation(i, yPos), yPos)

            // 如果手指抬起
            if (isUp) {
                mListener?.onTouchActionUp(constChar[mChooseIndex])
                isUp = false
            }
            mPaint.reset()
        }
    }

    /**
     * @param canvas  画板
     * @param string  被绘制的字母
     * @param xCenter 字母的中心x方向位置
     * @param yCenter 字母的中心y方向位置
     */
    private fun drawTextInCenter(canvas: Canvas, string: String, xCenter: Float, yCenter: Float) {

        val fm = mPaint.fontMetrics
        val fontHeight = mPaint.fontSpacing

        var drawY = yCenter + fontHeight / 2 - fm.descent

        if (drawY < -fm.ascent - fm.descent)
            drawY = -fm.ascent - fm.descent

        if (drawY > height)
            drawY = height.toFloat()

        mPaint.textAlign = Paint.Align.CENTER

        canvas.drawText(string, xCenter, drawY, mPaint)
    }

    private fun adjustFontSize(i: Int, yPos: Float): Int {

        // 根据水平方向偏移量计算出一个放大的字号
        val adjustX = Math.abs(ajustXPosAnimation(i, yPos))

        return ((mMaxFontSize - mMinFontSize) * adjustX / mMaxBezierHeight).toInt() + mMinFontSize
    }

    /**
     * x 方向的向左偏移量
     *
     * @param i    当前字母的索引
     * @param yPos y方向的初始位置  会变化
     * @return
     */
    private fun ajustXPosAnimation(i: Int, yPos: Float): Float {

        var offset: Float
        if (this.mAnimating || this.mHideAnimation) {
            // 正在动画中或在做隐藏动画
            offset = mLastOffset!![i]
            if (offset != 0.0f) {
                offset += this.mAnimationOffset
                if (offset > 0)
                    offset = 0f
            }
        } else {

            // 根据当前字母y方向位置, 计算水平方向偏移量
            offset = adjustXPos(yPos)

            // 当前触摸的x方向位置
            val xPos = mTouch.x

            var width = width - mWidthOffset
            width = width - 60

            // 字母绘制时向左偏移量 进行修正, offset需要是<=0的值
            if (offset != 0.0f && xPos > width) {
                offset += xPos - width
            }
            if (offset > 0) {
                offset = 0f
            }

            mLastOffset?.set(i, offset)
        }
        return offset
    }

    private fun adjustXPos(yPos: Float): Float {

        val dis = yPos - mTouch.y // 字母y方向位置和触摸时y值坐标的差值, 距离越小, 得到的水平方向偏差越大
        if (dis > -mMaxBezierWidth && dis < mMaxBezierWidth) {
            // 在2个贝赛尔曲线宽度范围以内 (一个贝赛尔曲线宽度是指一个山峰的一边)

            // 第一段 曲线
            if (dis > mMaxBezierWidth / 4) {
                for (i in mMaxBezierLines - 1 downTo 1) {
                    // 从下到上, 逐个计算

                    if (dis == -mBezier1[i].y)
                    // 落在点上
                        return mBezier1[i].x

                    // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                    if (dis > -mBezier1[i].y && dis < -mBezier1[i - 1].y) {
                        return (dis + mBezier1[i].y) * (mBezier1[i - 1].x - mBezier1[i].x) / (-mBezier1[i - 1].y + mBezier1[i].y) + mBezier1[i].x
                    }
                }
                return mBezier1[0].x
            }

            // 第三段 曲线, 和第一段曲线对称
            if (dis < -mMaxBezierWidth / 4) {
                for (i in 0 until mMaxBezierLines - 1) {
                    // 从上到下

                    if (dis == mBezier1[i].y)
                    // 落在点上
                        return mBezier1[i].x

                    // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                    if (dis > mBezier1[i].y && dis < mBezier1[i + 1].y) {
                        return (dis - mBezier1[i].y) * (mBezier1[i + 1].x - mBezier1[i].x) / (mBezier1[i + 1].y - mBezier1[i].y) + mBezier1[i].x
                    }
                }
                return mBezier1[mMaxBezierLines - 1].x
            }

            // 第二段 峰顶曲线
            for (i in 0 until mMaxBezierLines - 1) {

                if (dis == mBezier2[i].y)
                    return mBezier2[i].x

                // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                if (dis > mBezier2[i].y && dis < mBezier2[i + 1].y) {
                    return (dis - mBezier2[i].y) * (mBezier2[i + 1].x - mBezier2[i].x) / (mBezier2[i + 1].y - mBezier2[i].y) + mBezier2[i].x
                }
            }
            return mBezier2[mMaxBezierLines - 1].x

        }

        return 0.0f
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        val oldmChooseIndex = mChooseIndex
        val listener = mListener
        /**
         * 计算除去paddingTop后，用户点击不同位置对应的字母索引
         */
        val c = ((y - mPaddingTop) / (height - mPaddingTop - mPaddingBottom) * constChar.size).toInt()


        when (action) {
            MotionEvent.ACTION_DOWN -> {

                if (this.width > mWidthOffset) {
                    if (event.x < this.width - mWidthOffset)
                        return false
                }

                if (y < mPaddingTop || c < 0 || y > height - mPaddingBottom) {
                    return false
                }

                mHideWaitingHandler.removeMessages(1)

                mScroller!!.abortAnimation()
                mAnimating = false
                mHideAnimation = false
                mAlpha = 255

                mTouch.x = event.x
                mTouch.y = event.y

                if (oldmChooseIndex != c && listener != null) {
                    if (c > 0 && c < constChar.size) {
                        listener.onTouchLetterChanged(constChar[c], c)
                        mChooseIndex = c
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                mTouch.x = event.x
                mTouch.y = event.y
                invalidate()
                if (oldmChooseIndex != c && listener != null) {

                    if (c >= 0 && c < constChar.size) {
                        listener.onTouchLetterChanged(constChar[c], c)
                        mChooseIndex = c
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mTouch.x = event.x
                mTouch.y = event.y

                isUp = true
                mScroller!!.startScroll(0, 0, mMaxBezierHeight.toInt(), 0, 2000)
                mAnimating = true
                postInvalidate()
            }
        }
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {
            if (mAnimating) {
                val x = mScroller!!.currX.toFloat()
                mAnimationOffset = x
            } else if (mHideAnimation) {
                mAlpha = 255 - mScroller!!.currX
            }
            invalidate()
        } else if (mScroller!!.isFinished) {
            if (mAnimating) {
                mHideWaitingHandler.sendEmptyMessage(1)
            } else if (mHideAnimation) {
                mHideAnimation = false
                this.mChooseIndex = -1
                mTouch.x = -10000f
                mTouch.y = -10000f
            }

        }
    }

    fun setOnTouchLetterChangedListener(listener: OnTouchLetterChangedListener) {
        this.mListener = listener
    }

    /**
     * 计算出所有贝塞尔曲线上的点
     * 个数为 mMaxBezierLines * 2 = 64
     */
    private fun calculateBezierPoints() {

        val mStart = PointF()   // 开始点
        val mEnd = PointF()        // 结束点
        val mControl = PointF() // 控制点


        // 计算第一段红色部分 贝赛尔曲线的点
        // 开始点
        mStart.x = 0.0f
        mStart.y = -mMaxBezierWidth

        // 控制点
        mControl.x = 0.0f
        mControl.y = -mMaxBezierWidth / 2

        // 结束点
        mEnd.x = -mMaxBezierHeight / 2
        mEnd.y = -mMaxBezierWidth / 4

        mBezier1[0] = PointF()
        mBezier1[mMaxBezierLines - 1] = PointF()

        mBezier1[0].set(mStart)
        mBezier1[mMaxBezierLines - 1].set(mEnd)

        for (i in 1 until mMaxBezierLines - 1) {

            mBezier1[i] = PointF()

            mBezier1[i].x = calculateBezier(mStart.x, mEnd.x, mControl.x, i / mMaxBezierLines.toFloat())
            mBezier1[i].y = calculateBezier(mStart.y, mEnd.y, mControl.y, i / mMaxBezierLines.toFloat())

        }

        // 计算第二段蓝色部分 贝赛尔曲线的点
        mStart.y = -mMaxBezierWidth / 4
        mStart.x = -mMaxBezierHeight / 2

        mControl.y = 0.0f
        mControl.x = -mMaxBezierHeight

        mEnd.y = mMaxBezierWidth / 4
        mEnd.x = -mMaxBezierHeight / 2

        mBezier2[0] = PointF()
        mBezier2[mMaxBezierLines - 1] = PointF()

        mBezier2[0].set(mStart)
        mBezier2[mMaxBezierLines - 1].set(mEnd)

        for (i in 1 until mMaxBezierLines - 1) {

            mBezier2.let { it[i] = PointF() }
            mBezier2[i].x = calculateBezier(mStart.x, mEnd.x, mControl.x, i / mMaxBezierLines.toFloat())
            mBezier2[i].y = calculateBezier(mStart.y, mEnd.y, mControl.y, i / mMaxBezierLines.toFloat())
        }
    }

    /**
     * 贝塞尔曲线核心算法
     *
     * @param start
     * @param end
     * @param control
     * @param val
     * @return 公式及动图, 维基百科: https://en.wikipedia.org/wiki/B%C3%A9zier_curve
     * 中文可参考此网站: http://blog.csdn.net/likendsl/article/details/7852658
     */
    private fun calculateBezier(start: Float, end: Float, control: Float, `val`: Float): Float {

        val s = 1 - `val`

        return start * s * s + 2f * control * s * `val` + end * `val` * `val`
    }


}
