package com.dragon.dragcardsview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by dionysis_lorentzos on 5/8/14 for package com.lorentzos.swipecards
 * and project Swipe cards. Use with caution dinosaurs might appear!
 */

public class DragCardsView extends AdapterView {

	private int MAX_VISIBLE = 3;
	private int MIN_ADAPTER_STACK = 6;
	private float ROTATION_DEGREES = 15.f;
	public Adapter mAdapter;
	private int LAST_OBJECT_IN_STACK = 0;
	private final String TAG = "DragCardsView";
	private onDragListener mFlingListener;
	private AdapterDataSetObserver mDataSetObserver;
	private boolean mInLayout = false;
	private View mActiveCard = null;
	private OnItemClickListener mOnItemClickListener;
	private DragCardListener dragCardListener;
	private int CARDS_SHIFT;
	private int childLeft;
	private int childTop;
	public View mActiveCard2;
	public View mActiveCard3;
	private int widthMeasureSpec;
	private int heightMeasureSpec;
	private int CARDS_WIDTH;
	private int CARDS_HEIGHT;

	public DragCardsView(Context context) {
		this(context, null);
	}

	public DragCardsView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.SwipeFlingStyle);
	}

	public DragCardsView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DragCardsView, defStyle, 0);
		MAX_VISIBLE = a.getInt(R.styleable.DragCardsView_max_visible,
				MAX_VISIBLE);
		MIN_ADAPTER_STACK = a.getInt(
				R.styleable.DragCardsView_min_adapter_stack,
				MIN_ADAPTER_STACK);
		ROTATION_DEGREES = a.getFloat(
				R.styleable.DragCardsView_rotation_degrees,
				ROTATION_DEGREES);
		CARDS_SHIFT = a.getInt(R.styleable.DragCardsView_cards_shift,
				10);
		CARDS_WIDTH = a.getDimensionPixelSize(R.styleable.DragCardsView_cards_width, 300);
		CARDS_HEIGHT = a.getDimensionPixelSize(R.styleable.DragCardsView_cards_height, 330);
		a.recycle();
	}

	public void init(final Context context, Adapter mAdapter) {
		if (context instanceof onDragListener) {
			mFlingListener = (onDragListener) context;
		} else {
			throw new RuntimeException(
					"Activity does not implement SwipeFlingAdapterView.onFlingListener");
		}
		if (context instanceof OnItemClickListener) {
			mOnItemClickListener = (OnItemClickListener) context;
		}
		setAdapter(mAdapter);
	}

	public View getFirstCard() {
		return mActiveCard;
	}
	
	public View getSecondCard() {
		return mActiveCard2;
	}
	
	public View getThirdCard() {
		return mActiveCard3;
	}
	
	public void rotationLeft(){
		if(dragCardListener!=null){
			dragCardListener.rotationLeft();
		}
	}
	
	public void rotationtRight(){
		if(dragCardListener!=null){
			dragCardListener.rotationtRight();
		}
	}
	
	/**
	 * Call this when something has changed which has invalidated the layout of
	 * this view. 当view确定自身已经不再适合现有的区域时调用
	 * requestLayout()回去调用onMeasure()和onLayout()方法
	 */
	@Override
	public void requestLayout() {
		if (!mInLayout) {
			super.requestLayout();
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.widthMeasureSpec = widthMeasureSpec;
		this.heightMeasureSpec = heightMeasureSpec;
	}

	public int getWidthMeasureSpec() {
		return widthMeasureSpec;
	}

	public int getHeightMeasureSpec() {
		return heightMeasureSpec;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// if we don't have an adapter, we don't need to do anything
		if (mAdapter == null) {
			return;
		}
		mInLayout = true;
		final int adapterCount = mAdapter.getCount();
		if (adapterCount == 0) {
			removeAllViewsInLayout();
		} else {
			removeAllViewsInLayout();
			layoutChildren(0, adapterCount);
			setTopView();
		}
		mInLayout = false;
	}

	public void layoutChildren(int startingIndex, int adapterCount) {
		while (startingIndex < Math.min(adapterCount, MAX_VISIBLE)) {
			AntiAliasView newUnderChild = (AntiAliasView)mAdapter.getView(startingIndex, null, this);
			if (newUnderChild.getVisibility() != GONE) {
				makeAndAddView(newUnderChild, startingIndex);
				LAST_OBJECT_IN_STACK = startingIndex;
			}
			startingIndex++;
		}
	}

	public Adapter getmAdapter() {
		return mAdapter;
	}

	public void setmAdapter(Adapter mAdapter) {
		this.mAdapter = mAdapter;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void makeAndAddView(View child, int index) {
		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
		
		if (index < 3) {
			lp.width = CARDS_WIDTH - (index * CARDS_SHIFT * 2);
			lp.height = CARDS_HEIGHT - (index * CARDS_SHIFT * 2);
		} else {
			lp.width = CARDS_WIDTH - (2 * CARDS_SHIFT * 2);
			lp.height = CARDS_HEIGHT - (2 * CARDS_SHIFT * 2);
		}
		addViewInLayout(child, 0, lp, true);
		final boolean needToMeasure = child.isLayoutRequested();
		if (needToMeasure) {
			int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
					getPaddingLeft() + getPaddingRight() + lp.leftMargin
							+ lp.rightMargin, lp.width);
			int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
					getPaddingTop() + getPaddingBottom() + lp.topMargin
							+ lp.bottomMargin, lp.height);
			child.measure(childWidthSpec, childHeightSpec);

		} else {
			cleanupLayoutState(child);
		}

		int w = child.getMeasuredWidth();
		int h = child.getMeasuredHeight();

		int gravity = lp.gravity;
		if (gravity == -1) {
			gravity = Gravity.TOP | Gravity.START;
		}

		int layoutDirection = getLayoutDirection();
		final int absoluteGravity = Gravity.getAbsoluteGravity(gravity,
				layoutDirection);
		final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
		case Gravity.CENTER_HORIZONTAL:
			childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - w)
					/ 2 + lp.leftMargin - lp.rightMargin;
			break;
		case Gravity.END:
			childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
			break;
		case Gravity.START:
			
		default:
			childLeft = getPaddingLeft() + lp.leftMargin;
			break;
		}
		switch (verticalGravity) {
		case Gravity.CENTER_VERTICAL:
			childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - h)
					/ 2 + lp.topMargin - lp.bottomMargin;
			break;
		case Gravity.BOTTOM:
			childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
			break;
		case Gravity.TOP:
		default:
			childTop = getPaddingTop() + lp.topMargin;
			break;
		}
		if (index < 3) {
			child.layout(childLeft,
					childTop + index * CARDS_SHIFT + CARDS_HEIGHT - h, childLeft + w,
					childTop + index * CARDS_SHIFT + CARDS_HEIGHT);
		} else {
			child.layout(childLeft, childTop + 2 * CARDS_SHIFT + CARDS_HEIGHT - h,
					childLeft + w, childTop + 2 * CARDS_SHIFT + CARDS_HEIGHT);
		}
		if (index == 1) {
			this.mActiveCard2 = child;
		}
		if (index == 2) {
			this.mActiveCard3 = child;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	
	
	/**
	 * Set the top view and add the fling listener
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setTopView() {
		if (getChildCount() > 0) {
			mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
			final ImageView iv_dislike = (ImageView) mActiveCard.findViewById(R.id.iv_dislike);
			final ImageView iv_like = (ImageView) mActiveCard.findViewById(R.id.iv_like);
			if (mActiveCard != null) {
				dragCardListener = new DragCardListener(this,
						mAdapter.getItem(0), ROTATION_DEGREES,
						new DragCardListener.FlingListener() {
							@Override
							public void onCardExited(boolean isLeft) {
								mActiveCard = null;
								if (mFlingListener != null) {
									mFlingListener.removeFirstObjectInAdapter(isLeft);
									if (getChildCount() < MAX_VISIBLE) {
										mFlingListener.onAdapterAboutToEmpty(getChildCount());
									}
								}
							}

							@Override
							public void onCardClick(Object dataObject) {
								if (mOnItemClickListener != null)
									mOnItemClickListener.onItemClicked(0,dataObject);
							}

							@Override
							public void onSelectLeft(double distance) {
								// TODO Auto-generated method stub
								iv_dislike.setVisibility(View.VISIBLE);
								iv_like.setVisibility(View.INVISIBLE);
								if (mFlingListener != null) {
									mFlingListener.onSelectLeft(distance);
								}
							}

							@Override
							public void onSelectRight(double distance) {
								// TODO Auto-generated method stub
								iv_like.setVisibility(View.VISIBLE);
								iv_dislike.setVisibility(View.INVISIBLE);
								if (mFlingListener != null) {
									mFlingListener.onSelectRight(distance);
								}
							}

							@Override
							public void onCardMoveDistance(double distance) {
								// TODO Auto-generated method stub
								if (iv_like.isShown()) {
									iv_like.setAlpha((float) distance /dragCardListener.MAX_DISTANCE);
								} else if (iv_dislike.isShown()) {
									iv_dislike.setAlpha((float) distance / dragCardListener.MAX_DISTANCE);
								}
								if (mFlingListener != null) {
									mFlingListener.onCardMoveDistance(distance);
								}
							}

							@Override
							public void onCardReturn() {
								// TODO Auto-generated method stub
								iv_dislike.setVisibility(View.INVISIBLE);
								iv_like.setVisibility(View.INVISIBLE);
								if (mFlingListener != null) {
									mFlingListener.onCardReturn();
								}
							}
						});
				dragCardListener.setViewGroudWidth(this.getWidth());
				dragCardListener.setViewGroudHeight(this.getHeight());
				mActiveCard.setOnTouchListener(dragCardListener);
			}
		}
	}
	

	public int getCARDS_SHIFT() {
		return CARDS_SHIFT;
	}

	public void setCARDS_SHIFT(int cARDS_SHIFT) {
		CARDS_SHIFT = cARDS_SHIFT;
	}

	public void setMaxVisible(int MAX_VISIBLE) {
		this.MAX_VISIBLE = MAX_VISIBLE;
	}

	public void setMinStackInAdapter(int MIN_ADAPTER_STACK) {
		this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (mAdapter != null && mDataSetObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
			mDataSetObserver = null;
		}
		mAdapter = adapter;
		if (mAdapter != null && mDataSetObserver == null) {
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

		}
	}

	public void setFlingListener(onDragListener onFlingListener) {
		this.mFlingListener = onFlingListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LinearLayout.LayoutParams(getContext(), attrs);
	}

	private class AdapterDataSetObserver extends DataSetObserver {
		/**
		 * This method is called when the entire data set has changed
		 * 调用此方法时,整个数据集已经改变了
		 */
		@Override
		public void onChanged() {
			requestLayout();
		}

		/**
		 * This method is called when the entire data becomes invalid
		 * 调用此方法时,整个数据变得无效
		 */
		@Override
		public void onInvalidated() {
			requestLayout();
		}

	}

	public interface OnItemClickListener {
		public void onItemClicked(int itemPosition, Object dataObject);
	}

	public interface onDragListener {
		public void removeFirstObjectInAdapter(boolean isLeft);

		public void onSelectLeft(double distance);

		public void onSelectRight(double distance);

		public void onAdapterAboutToEmpty(int itemsInAdapter);

		public void onCardMoveDistance(double distance);

		public void onCardReturn();
	}

	@Override
	public void setSelection(int position) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public View getSelectedView() {
		// TODO Auto-generated method stub
		return getFirstCard();
	}

}
