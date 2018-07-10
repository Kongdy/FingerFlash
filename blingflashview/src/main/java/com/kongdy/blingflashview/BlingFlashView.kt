package com.kongdy.blingflashview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

/**
 *  @author kongdy
 *  @date 2018-07-06 13:51
 *  @TIME 13:51
 *
 *  实现一些特殊动画的view
 *
 **/
class BlingFlashView : View {

    companion object {
        /**
         * 动画：进入模式
         */
        const val MODE_ENTER = 0x01
        /**
         * 动画：退出模式
         */
        const val MODE_EXIT = 0x02
    }
    /**
     * 覆盖在上层的图片
     */
    private var coverImg : Drawable? = null
    /**
     * 涂鸦画笔
     */
    private val defaultPaint = Paint()

    // 动画默认执行时间3秒
    private var animTime = 3000L

    private var componentInitReady = false
    /**
     * 动画默认为进入模式
     */
    private var animMode = MODE_ENTER

    /**
     * 动画处理类,做成接口利于后期扩展
     */
    private var blingAnim:BlingAnim = FingerFlashAnim()

    private var xfermode:PorterDuffXfermode? = null

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,-1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initComponent(context)
        applyAttrs(context,attrs)
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initComponent(context)
        applyAttrs(context,attrs)
    }

    /**
     * 初始化自定义view必要组件元素
     */
    private fun initComponent(context: Context?) {
        // 圆头画笔
        defaultPaint.strokeCap = Paint.Cap.ROUND
        // 线段交界处设为直角交接
        defaultPaint.strokeJoin = Paint.Join.BEVEL

        /*高画质处理*/
        // 抗锯齿
        defaultPaint.isAntiAlias = true
        // 图像滤波
        defaultPaint.isFilterBitmap = true
        // 像素防抖
        defaultPaint.isDither = true

        componentInitReady = true
    }

    private fun applyAttrs(context: Context?,attrs: AttributeSet?) {
        context?.let {
            val ta = context.obtainStyledAttributes(attrs,R.styleable.BlingFlashView)
            // 要用来执行动画的覆盖图片
            if(ta.hasValue(R.styleable.BlingFlashView_bfv_coverImg)) {
                coverImg = ta.getDrawable(R.styleable.BlingFlashView_bfv_coverImg)
            }
            // 动画执行时间
            if(ta.hasValue(R.styleable.BlingFlashView_bfv_animTime)) {
                animTime = ta.getInteger(R.styleable.BlingFlashView_bfv_animTime, 3000).toLong()
            }
            // 动画执行模式（目前主要由退出、进入构成）
            if(ta.hasValue(R.styleable.BlingFlashView_bfv_anim_mode)) {
                val animModeStr = ta.getString(R.styleable.BlingFlashView_bfv_anim_mode)
                if(TextUtils.equals(animModeStr,"1")) {
                    animMode = MODE_ENTER
                } else if(TextUtils.equals(animModeStr,"2")) {
                    animMode = MODE_EXIT
                }
            }
            ta?.recycle()
        }
        setXAnimMode(animMode)
    }

    private fun setXAnimMode(animMode:Int) {
        xfermode = when (animMode) {
            MODE_ENTER -> PorterDuffXfermode(PorterDuff.Mode.XOR)
            MODE_EXIT -> PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            else -> PorterDuffXfermode(PorterDuff.Mode.XOR)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        blingAnim.initSize(viewWidth,viewHeight)

        coverImg?.let {
            it.setBounds(0,0,viewWidth,viewHeight)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        // 离屏渲染
        canvas?.saveLayer(0F, 0F, width.toFloat(), height.toFloat(),defaultPaint,Canvas.ALL_SAVE_FLAG)
        coverImg?.let {
            it.draw(canvas)
            blingAnim.getPaint().xfermode = xfermode
            blingAnim.draw(canvas)
            blingAnim.getPaint().xfermode = null
        }
        canvas?.restore()
        if(!blingAnim.getIsAutoStartDo()) {
            blingAnim.start(animTime,this)
        }
    }

    open fun start() {
        blingAnim.start(animTime,this)
    }

}