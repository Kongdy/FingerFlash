package com.kongdy.blingflashview

import android.graphics.Canvas
import android.graphics.Paint

/**
 *  @author kongdy
 *  @date 2018-07-06 15:12
 *  @TIME 15:12
 *
 *
 **/
interface BlingAnim {
    /**
     * 自动动画执行是否已经开始过
     */
    fun getIsAutoStartDo():Boolean
    /**
     * 获取动画主要画笔
     */
    fun getPaint():Paint

    /**
     * 初始化大小，为画笔提供设置大小初始化
     */
    fun initSize(viewWidth:Int,viewHeight:Int)

    /**
     * 执行着色
     */
    fun draw(canvas: Canvas?)

    /**
     * 执行动画
     */
    fun start(animTime:Long,view:BlingFlashView)
}