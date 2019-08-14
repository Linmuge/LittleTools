package com.gushenge.testdemo.SwipeLeftOrRight


import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * 实现监听左右滑动的事件，哪个view需要的时候直接setOnTouchListener就可以用了
 * @author Gushenge
 */
open class GestureListener(context: Context) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {
    /** 左右滑动的最短距离  */
    var distance = 200
    /** 左右滑动的最大速度  */
    var velocity = 200

    var gestureDetector: GestureDetector? = null

    init {
        gestureDetector = GestureDetector(context, this)
    }

    /**
     * 向左滑的时候调用的方法，子类应该重写
     * @return
     */
    open fun left(): Boolean {
        return false
    }

    /**
     * 向右滑的时候调用的方法，子类应该重写
     * @return
     */
    open fun right(): Boolean {
        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                         velocityY: Float): Boolean {
        // TODO Auto-generated method stub
        // e1：第1个ACTION_DOWN MotionEvent
        // e2：最后一个ACTION_MOVE MotionEvent
        // velocityX：X轴上的移动速度（像素/秒）
        // velocityY：Y轴上的移动速度（像素/秒）

        // 向左滑
        if (e1.x - e2.x > distance && Math.abs(velocityX) > velocity) {
            left()
        }
        // 向右滑
        if (e2.x - e1.x > distance && Math.abs(velocityX) > velocity) {
            right()
        }
        return false
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        gestureDetector!!.onTouchEvent(event)
        return false
    }
}

