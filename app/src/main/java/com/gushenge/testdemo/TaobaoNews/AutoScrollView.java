package com.gushenge.testdemo.TaobaoNews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.IntDef;
import androidx.core.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

/**

* @Description: 自动滚动的ListView
*/
public class AutoScrollView extends ListView {
    public static final String TAG = "AutoScrollView";
	
	public final static int SCROLL_UP = 0x00;
	
	public final static int SCROLL_DOWN = 0x01;
	
	@IntDef({SCROLL_DOWN,SCROLL_UP})
	public @interface ScrollOritation{}
	
	private final static int DALY_TIME = 3000;

	private LoopRunnable mLoopRunnable;
	
	private boolean mAnimating = false;
	
	private Scroller mScroller;
	
	private InnerAdapter mInnerAdapter;
	
	private ListAdapter mOutterAdapter;
	
	private InnerOnItemClickListener mInnerOnItemClickListener;
	
	private OnItemClickListener mOutterOnItemClickListener;
	
	private InnerOnItemLongClickListener mInnerOnItemLongClickListener;
	
	private OnItemLongClickListener mOutterOnItemLongClickListener;
	
	private boolean mAutoScroll = false;
	
	/**
	 * 滚动方向，默认向上滚动。
	 */
	private int mScrollOrientation = SCROLL_UP;
	
	private float mMoveDistance = 0;
	
	private float mPreX = 0;
	
	private float mPreY = 0;
	
	private boolean mIgnoreLongClick = false;
	
	public AutoScrollView(Context context) {
		this(context, null);
	}
	
	public AutoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLoopRunnable = new LoopRunnable();
		mScroller = new Scroller(context, new AccelerateInterpolator());
		mInnerAdapter = new InnerAdapter();
	}
	
	public AutoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mAutoScroll && mOutterAdapter != null) {
			AutoScroll autoScroll = (AutoScroll)mOutterAdapter;
			int height = autoScroll.getListItemHeight(getContext()) * autoScroll.getImmovableCount() 
					+ (autoScroll.getImmovableCount() - 1) * getDividerHeight();
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAutoScroll = adapter instanceof AutoScroll;
		mOutterAdapter = adapter;
		super.setAdapter(mInnerAdapter);
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		if (mInnerOnItemClickListener == null) {
			mInnerOnItemClickListener = new InnerOnItemClickListener();
		}
		mOutterOnItemClickListener = listener;
		super.setOnItemClickListener(mInnerOnItemClickListener);
	}
	
	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		if (mInnerOnItemLongClickListener == null) {
			mInnerOnItemLongClickListener = new InnerOnItemLongClickListener();
		}
		mOutterOnItemLongClickListener = listener;
		super.setOnItemLongClickListener(mInnerOnItemLongClickListener);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.d(TAG, "onAttachedToWindow");
		postDelayed(mLoopRunnable, DALY_TIME);
		mAnimating = true;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(TAG, "onDetachedFromWindow");
		removeCallbacks(mLoopRunnable);
	}
	
	int preY = 0;
	@Override
	public void computeScroll() {
		Log.d(TAG, "computeScroll");
		if (!mScroller.computeScrollOffset()) {
			Log.d(TAG, "compute finish");
			if (mAnimating) {
				Log.d(TAG, "compute ignore runnable");
				return;
			}
			Log.d(TAG, "compute send runnable");
			removeCallbacks(mLoopRunnable);
			postDelayed(mLoopRunnable, DALY_TIME);
			mAnimating = true;
			preY = 0;
			checkPosition();
		}else {
			mAnimating = false;
			Log.d(TAG, "compute not finish");
			int dY = mScroller.getCurrY() - preY;
			ListViewCompat.scrollListBy(this, dY);
			preY = mScroller.getCurrY();
			invalidate();
		}
	}
	
	private void checkPosition() {
		if (!mAutoScroll) return;
		int targetPosition = -1;
		int firstVisiblePosition = getFirstVisiblePosition();
		if (firstVisiblePosition == 0) {
			AutoScroll autoScroll = (AutoScroll) mInnerAdapter;
			targetPosition = mInnerAdapter.getCount() - autoScroll.getImmovableCount() * 2;
		}
		int lastVisiblePosition = getLastVisiblePosition();
		if (lastVisiblePosition == getCount() - 1) {
			AutoScroll autoScroll = (AutoScroll) mOutterAdapter;
			targetPosition = autoScroll.getImmovableCount();
		}
		if (targetPosition >= 0 && firstVisiblePosition != targetPosition) {
			setSelection(targetPosition);
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mMoveDistance = 0;
			mPreX = ev.getX();
			mPreY = ev.getY();
			mIgnoreLongClick = false;
		}else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			mMoveDistance +=(Math.abs(ev.getX() - mPreX) + Math.abs(ev.getY() - mPreY));
			mPreX = ev.getX();
			mPreY = ev.getY();
			if (mMoveDistance > 20 || !mScroller.isFinished()) {
				mIgnoreLongClick = true;
			}
			return true;
		}else if (ev.getAction() == MotionEvent.ACTION_UP 
				|| ev.getAction() == MotionEvent.ACTION_CANCEL) {
			if (mMoveDistance > 20 || !mScroller.isFinished()) {
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			mIgnoreLongClick = false;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 设置滚动的防线
	 * @param oritation 滚动方向，SCROLL_UP 向上，SCROLL_DOWN 向下。
	 */
	public void setScrollOrientation(@ScrollOritation int oritation) {
		this.mScrollOrientation = oritation;
	}
	
	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		removeCallbacks(mLoopRunnable);
		mAnimating = false;
		post(mLoopRunnable);
	}
	
	/**
	 * 停止自动滚动
	 */
	public void stopAutoScroll() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		removeCallbacks(mLoopRunnable);
		mAnimating = false;
	}
	
	class LoopRunnable implements Runnable{

		@Override
		public void run() {
			Log.d(TAG, "run");
			mAnimating = true;
			View childAt = getChildAt(0);
			int scrollHeight = childAt.getMeasuredHeight() + getDividerHeight();
			mScroller.startScroll(0, 0, 0, mScrollOrientation == SCROLL_UP ? scrollHeight : -scrollHeight);
			invalidate();
		}
		
	}
	
	class InnerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOutterAdapter == null ? 0 : 
				(mAutoScroll ? mOutterAdapter.getCount() + ((AutoScroll)mOutterAdapter).getImmovableCount() * 2:mOutterAdapter.getCount());
		}

		@Override
		public Object getItem(int position) {
			return mOutterAdapter.getItem((int)getItemId(position));
		}

		@Override
		public long getItemId(int position) {
			if (mAutoScroll) {
				AutoScroll autoScroll = (AutoScroll) mOutterAdapter;
				int immovableCount = autoScroll.getImmovableCount();
				int outerCount = mOutterAdapter.getCount();
				if (position < immovableCount) {//第一组
					return outerCount - immovableCount + position;
				}else if (position < immovableCount + outerCount) {//第二组
					return position - immovableCount;
				}else {//第三组
					return position - (immovableCount + outerCount);
				}
			}else {
				return position;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mOutterAdapter.getView((int)getItemId(position), convertView, parent);
		}
		
	}
	
	class InnerOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mOutterOnItemClickListener != null && mInnerAdapter != null) {
				mOutterOnItemClickListener.onItemClick(parent, view, (int)mInnerAdapter.getItemId(position), id);
			}
		}
		
	}
	
	class InnerOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (mOutterOnItemLongClickListener != null && mInnerAdapter != null && !mIgnoreLongClick) {
				return mOutterOnItemLongClickListener.onItemLongClick(parent, view, (int)mInnerAdapter.getItemId(position), id);
			}
			return false;
		}
		
	}
	
	public interface AutoScroll{
		/**
		 * 返回屏幕可见个数
		 * @return 可见个数
		 */
		public int getImmovableCount();
		
		/**
		 * 获取条目高度
		 * @return
		 */
		public int getListItemHeight(Context context);
	}
}
