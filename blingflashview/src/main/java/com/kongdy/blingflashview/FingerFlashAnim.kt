package com.kongdy.blingflashview

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*

/**
 *  @author kongdy
 *  @date 2018-07-06 15:32
 *  @TIME 15:32
 *
 *  手指擦出动画
 *
 **/
class FingerFlashAnim : BlingAnim {
    private val doodlePaint = Paint()
    private val doodlePath = Path()
    private val doodlePathMeasure: PathMeasure = PathMeasure()
    private var doodlePathDistance = 0F
    private val animPath = Path()

    /**
     * 存储x轴开始动画执行节点
     */
    private val widthNodes = arrayOfNulls<Float>(10)
    /**
     * 存储y轴开始动画执行节点
     */
    private val heightNodes = arrayOfNulls<Float>(10)

    private var isAutoStartDo = false
    override fun getIsAutoStartDo(): Boolean = isAutoStartDo

    override fun initSize(viewWidth: Int, viewHeight: Int) {
        // 圆头画笔
        doodlePaint.strokeCap = Paint.Cap.ROUND
        // 线段交界处设为直角交接
        doodlePaint.strokeJoin = Paint.Join.ROUND

        /*高画质处理*/
        // 抗锯齿
        doodlePaint.isAntiAlias = true
        // 图像滤波
        doodlePaint.isFilterBitmap = true
        // 像素防抖
        doodlePaint.isDither = true

        // 求出对角线长度，其中每次步长为对角线长度的十分之一
        if (viewWidth > 0 && viewHeight > 0) {
            val diagonalDistance = Math.sqrt(((viewWidth * viewWidth) + (viewHeight * viewHeight)).toDouble())

            val animStep = ((diagonalDistance / 10).toFloat())
            doodlePaint.strokeWidth = animStep
            doodlePaint.style = Paint.Style.STROKE

            val widthStep = viewWidth / 5
            val heightStep = viewHeight / 5

            for (i in 0..4) {
                widthNodes[i] = widthStep / 2F + (i * widthStep)
                heightNodes[i] = heightStep / 2F + (i * heightStep)
            }

            doodlePath.reset()
            doodlePath.moveTo(0F,heightNodes[0]!!)
            var isLeft = true
            for (i in 0..4) {
                var topRange: Float
                var leftRange: Float
                val tempIndex = if (i > 4) {
                    topRange = viewHeight.toFloat()
                    leftRange = viewWidth.toFloat()
                    isLeft = false
                    i - 5
                } else {
                    topRange = 0F
                    leftRange = 0F
                    i
                }
                isLeft = if (isLeft) {
                    doodlePath.lineTo(leftRange, heightNodes[tempIndex]!!)
                    doodlePath.lineTo(widthNodes[tempIndex]!!, topRange)
                    false
                } else {
                    doodlePath.lineTo(widthNodes[tempIndex]!!, topRange)
                    doodlePath.lineTo(leftRange, heightNodes[tempIndex]!!)
                    true
                }
            }

            doodlePathMeasure.setPath(doodlePath, false)
            doodlePathDistance = doodlePathMeasure.length
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.drawPath(animPath, doodlePaint)
    }

    override fun start(animTime: Long, view: BlingFlashView) {
        isAutoStartDo = true
        animPath.reset()
        val valueAnimator = ValueAnimator.ofFloat(0F, 1F)
        valueAnimator.addUpdateListener({
            val animCurrentValue = it.animatedValue as Float
            doodlePathMeasure.getSegment(0F, animCurrentValue * doodlePathDistance, animPath, true)
            view.invalidate()
        })
        valueAnimator.duration = animTime
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                // view.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
                //  view.visibility = View.GONE
            }
        })
        valueAnimator.startDelay = 300L
        valueAnimator.start()
    }

    override fun getPaint(): Paint {
        return doodlePaint
    }

}