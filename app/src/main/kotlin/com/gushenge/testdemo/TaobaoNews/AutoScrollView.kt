package com.gushenge.testdemo.TaobaoNews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import androidx.annotation.IntDef
import androidx.core.widget.ListViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Scroller

/**
 *
 * @Description: 自动滚动的ListView
 */
class AutoScrollView : ListView {

    private var mLoopRunnable: LoopRunnable? = null

    private var mAnimating = false

    lateinit var mScroller: Scroller

    private var mInnerAdapter: InnerAdapter? = null

    private var mOutterAdapter: ListAdapter? = null

    private var mInnerOnItemClickListener: InnerOnItemClickListener? = null

    private var mOutterOnItemClickListener: OnItemClickListener? = null

    private var mInnerOnItemLongClickListener: InnerOnItemLongClickListener? = null

    private var mOutterOnItemLongClickListener: OnItemLongClickListener? = null

    private var mAutoScroll = false

    /**
     * 滚动方向，默认向上滚动。
     */
    private var mScrollOrientation = SCROLL_UP

    private var mMoveDistance = 0f

    private var mPreX = 0f

    private var mPreY = 0f

    private var mIgnoreLongClick = false

    internal var preY = 0
    @IntDef(SCROLL_DOWN, SCROLL_UP)
    annotation class ScrollOritation{}




    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mLoopRunnable = LoopRunnable()
        mScroller = Scroller(context, AccelerateInterpolator())
        mInnerAdapter = InnerAdapter()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (mAutoScroll && mOutterAdapter != null) {
            val autoScroll = mOutterAdapter as AutoScroll?
            val height = autoScroll!!.getListItemHeight(context) * autoScroll.immovableCount + (autoScroll.immovableCount - 1) * dividerHeight
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setAdapter(adapter: ListAdapter) {
        mAutoScroll = adapter is AutoScroll
        mOutterAdapter = adapter
        super.setAdapter(mInnerAdapter)
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        if (mInnerOnItemClickListener == null) {
            mInnerOnItemClickListener = InnerOnItemClickListener()
        }
        mOutterOnItemClickListener = listener
        super.setOnItemClickListener(mInnerOnItemClickListener)
    }

    override fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        if (mInnerOnItemLongClickListener == null) {
            mInnerOnItemLongClickListener = InnerOnItemLongClickListener()
        }
        mOutterOnItemLongClickListener = listener
        super.setOnItemLongClickListener(mInnerOnItemLongClickListener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow")
        postDelayed(mLoopRunnable, DALY_TIME.toLong())
        mAnimating = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "onDetachedFromWindow")
        removeCallbacks(mLoopRunnable)
    }

    override fun computeScroll() {
        Log.d(TAG, "computeScroll")
        if (!mScroller.computeScrollOffset()) {
            Log.d(TAG, "compute finish")
            if (mAnimating) {
                Log.d(TAG, "compute ignore runnable")
                return
            }
            Log.d(TAG, "compute send runnable")
            removeCallbacks(mLoopRunnable)
            postDelayed(mLoopRunnable, DALY_TIME.toLong())
            mAnimating = true
            preY = 0
            checkPosition()
        } else {
            mAnimating = false
            Log.d(TAG, "compute not finish")
            val dY = mScroller.currY - preY
            ListViewCompat.scrollListBy(this, dY)
            preY = mScroller.currY
            invalidate()
        }
    }

    private fun checkPosition() {
        if (!mAutoScroll) return
        var targetPosition = -1
        val firstVisiblePosition = firstVisiblePosition
        if (firstVisiblePosition == 0) {
            val autoScroll = mInnerAdapter as AutoScroll?
            targetPosition = mInnerAdapter!!.count - autoScroll!!.immovableCount * 2
        }
        val lastVisiblePosition = lastVisiblePosition
        if (lastVisiblePosition == count - 1) {
            val autoScroll = mOutterAdapter as AutoScroll?
            targetPosition = autoScroll!!.immovableCount
        }
        if (targetPosition >= 0 && firstVisiblePosition != targetPosition) {
            setSelection(targetPosition)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            mMoveDistance = 0f
            mPreX = ev.x
            mPreY = ev.y
            mIgnoreLongClick = false
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            mMoveDistance += Math.abs(ev.x - mPreX) + Math.abs(ev.y - mPreY)
            mPreX = ev.x
            mPreY = ev.y
            if (mMoveDistance > 20 || !mScroller.isFinished) {
                mIgnoreLongClick = true
            }
            return true
        } else if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
            if (mMoveDistance > 20 || !mScroller.isFinished) {
                ev.action = MotionEvent.ACTION_CANCEL
            }
            mIgnoreLongClick = false
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 设置滚动的防线
     * @param oritation 滚动方向，SCROLL_UP 向上，SCROLL_DOWN 向下。
     */
    fun setScrollOrientation(@ScrollOritation oritation: Int) {
        this.mScrollOrientation = oritation
    }

    /**
     * 开始自动滚动
     */
    fun startAutoScroll() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
        removeCallbacks(mLoopRunnable)
        mAnimating = false
        post(mLoopRunnable)
    }

    /**
     * 停止自动滚动
     */
    fun stopAutoScroll() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
        removeCallbacks(mLoopRunnable)
        mAnimating = false
    }

    internal inner class LoopRunnable : Runnable {

        override fun run() {
            Log.d(TAG, "run")
            mAnimating = true
            val childAt = getChildAt(0)
            val scrollHeight = childAt.measuredHeight + dividerHeight
            mScroller.startScroll(0, 0, 0, if (mScrollOrientation == SCROLL_UP) scrollHeight else -scrollHeight)
            invalidate()
        }

    }

    internal inner class InnerAdapter : BaseAdapter() {
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            return mOutterAdapter!!.getView(getItemId(p0).toInt(), p1, p2)
        }

        override fun getCount(): Int {
            return if (mOutterAdapter == null)
                0
            else
                if (mAutoScroll) mOutterAdapter!!.count + (mOutterAdapter as AutoScroll).immovableCount * 2 else mOutterAdapter!!.count
        }

        override fun getItem(position: Int): Any {
            return mOutterAdapter!!.getItem(getItemId(position).toInt())
        }

        override fun getItemId(position: Int): Long {
            if (mAutoScroll) {
                val autoScroll = mOutterAdapter as AutoScroll?
                val immovableCount = autoScroll!!.immovableCount
                val outerCount = mOutterAdapter!!.count
                return if (position < immovableCount) {//第一组
                    (outerCount - immovableCount + position).toLong()
                } else if (position < immovableCount + outerCount) {//第二组
                    (position - immovableCount).toLong()
                } else {//第三组
                    (position - (immovableCount + outerCount)).toLong()
                }
            } else {
                return position.toLong()
            }
        }

    }

    internal inner class InnerOnItemClickListener : AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                                 id: Long) {
            if (mOutterOnItemClickListener != null && mInnerAdapter != null) {
                mOutterOnItemClickListener!!.onItemClick(parent, view, mInnerAdapter!!.getItemId(position).toInt(), id)
            }
        }

    }

    internal inner class InnerOnItemLongClickListener : AdapterView.OnItemLongClickListener {

        override fun onItemLongClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long): Boolean {
            return if (mOutterOnItemLongClickListener != null && mInnerAdapter != null && !mIgnoreLongClick) {
                mOutterOnItemLongClickListener!!.onItemLongClick(parent, view, mInnerAdapter!!.getItemId(position).toInt(), id)
            } else false
        }

    }

    interface AutoScroll {
        /**
         * 返回屏幕可见个数
         * @return 可见个数
         */
        val immovableCount: Int

        /**
         * 获取条目高度
         * @return
         */
        fun getListItemHeight(context: Context): Int
    }

    companion object {
        val TAG = "AutoScrollView"

        const val SCROLL_UP = 0x00

        const val SCROLL_DOWN = 0x01

        private val DALY_TIME = 3000
    }
}
