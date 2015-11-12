package com.dragon.dragcardsview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

/**
 * Created by dionysis_lorentzos on 5/8/14 for package com.lorentzos.swipecards
 * and project Swipe cards. Use with caution dinausaurs might appear!
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class DragCardListener implements View.OnTouchListener {
	private final float objectX;
	private final float objectY;
	private final int objectH;
	private final int objectW;
	private final int parentWidth;
	private final FlingListener mFlingListener;
	private final Object dataObject;
	private final float halfWidth;
	private float BASE_ROTATION_DEGREES;
	public static int MAX_DISTANCE = 255;
	private float aPosX;
	private float aPosY;
	private float aDownTouchX;
	private float aDownTouchY;
	private static final int INVALID_POINTER_ID = -1;
	private final String TAG = "FlingCardListener";
	// The active pointer is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;
	private final int TOUCH_ABOVE = 0;
	private final int TOUCH_BELOW = 1;
	private int touchPosition;
	private View firstCard;
	private boolean isAnimationRunning = false;
	private float MAX_COS = (float) Math.cos(Math.toRadians(45));
	private float ViewGroudWidth;
	private float ViewGroudHeight;
	private DragCardsView parent;
	private float objectX2;
	private float objectY2;
	private float objectX3;
	private float objectY3;
	private float firstX;
	private float firstY;
	private int CARDS_SHIFT;

	private Runnable task = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (firstCard != null) {
				double distance = getDistance();
				if (mFlingListener != null) {
					mFlingListener.onCardMoveDistance(distance);
				}
				if (parent.getSecondCard() != null) {
					parent.getSecondCard().setScaleX(
							(float) firstCard.getWidth()
									/ (float) (firstCard.getWidth() - distance
											* CARDS_SHIFT / MAX_DISTANCE * 2));
					parent.getSecondCard().setScaleY(
							(float) firstCard.getHeight()
									/ (float) (firstCard.getHeight() - distance
											* CARDS_SHIFT / MAX_DISTANCE * 2));
					parent.getSecondCard().setY(
							(float) (objectY2 - distance * CARDS_SHIFT * 2
									/ MAX_DISTANCE));
				}
				if (parent.getThirdCard() != null) {
					parent.getThirdCard().setScaleX(
							(float) (firstCard.getWidth() / (firstCard
									.getWidth() - distance * CARDS_SHIFT
									/ MAX_DISTANCE * 2)));
					parent.getThirdCard().setScaleY(
							(float) (firstCard.getHeight() / (firstCard
									.getHeight() - distance * CARDS_SHIFT
									/ MAX_DISTANCE * 2)));
					parent.getThirdCard().setY(
							(float) (objectY3 - distance * CARDS_SHIFT * 2
									/ MAX_DISTANCE));
				}
				firstCard.post(this);
			}
		}
	};

	public float getViewGroudWidth() {
		return ViewGroudWidth;
	}

	public void setViewGroudWidth(float viewGroudWidth) {
		ViewGroudWidth = viewGroudWidth;
	}

	public float getViewGroudHeight() {
		return ViewGroudHeight;
	}

	public void setViewGroudHeight(float viewGroudHeight) {
		ViewGroudHeight = viewGroudHeight;
	}

	public DragCardListener(DragCardsView parent, Object itemAtPosition,
			FlingListener flingListener) {
		this(parent, itemAtPosition, 15f, flingListener);
	}

	public DragCardListener(DragCardsView parent, Object itemAtPosition,
			float rotation_degrees, FlingListener flingListener) {
		super();
		this.parent = parent;
		this.firstCard = parent.getFirstCard();
		this.CARDS_SHIFT = parent.getCARDS_SHIFT();
		this.objectX = parent.getFirstCard().getX();
		this.objectY = parent.getFirstCard().getY();
		this.objectX2 = parent.getSecondCard().getX();
		this.objectY2 = parent.getSecondCard().getY();
		this.objectX3 = parent.getThirdCard().getX();
		this.objectY3 = parent.getThirdCard().getY();
		this.objectH = parent.getFirstCard().getHeight();
		this.objectW = parent.getFirstCard().getWidth();
		this.halfWidth = objectW / 2f;
		this.MAX_DISTANCE=parent.getWidth()/2;
		this.dataObject = itemAtPosition;
		this.parentWidth = parent.getWidth();
		this.BASE_ROTATION_DEGREES = rotation_degrees;
		this.mFlingListener = flingListener;
	}

	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			aDownTouchX = x;
			aDownTouchY = y;
			if (firstX == 0) {
				firstX = firstCard.getX();
			}
			if (firstY == 0) {
				firstY = firstCard.getY();
			}
			if (aPosX == 0) {
				aPosX = firstCard.getX();
			}
			if (aPosY == 0) {
				aPosY = firstCard.getY();
			}
			if (y < (objectY + objectH / 2.f)) {
				touchPosition = TOUCH_ABOVE;
			} else {
				touchPosition = TOUCH_BELOW;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float xMove = event.getX();
			float yMove = event.getY();
			float dx = xMove - aDownTouchX;
			float dy = yMove - aDownTouchY;
			aPosX += dx;
			aPosY += dy;
			float distobjectX = aPosX - objectX;
			float rotation = BASE_ROTATION_DEGREES * 1.f * distobjectX
					/ parentWidth;
			if (touchPosition == TOUCH_BELOW) {
				rotation = -rotation * 0.2f;
			}
			double distance = getDistance();
			float maringLeft = firstCard.getX();
			float marginRight = ViewGroudWidth
					- (firstCard.getX() + firstCard.getWidth());
			if (maringLeft < marginRight) {
				if (mFlingListener != null) {
					mFlingListener.onSelectLeft(distance);
				}
			} else if (maringLeft > marginRight) {
				if (mFlingListener != null) {
					mFlingListener.onSelectRight(distance);
				}
			}
			firstCard.setX(aPosX);
			firstCard.setY(aPosY);
			firstCard.setRotation(rotation);

			float sx = (float) firstCard.getWidth()
					/ (float) (firstCard.getWidth() - distance * CARDS_SHIFT
							/ MAX_DISTANCE * 2);
			float sy = (float) firstCard.getHeight()
					/ (float) (firstCard.getHeight() - distance * CARDS_SHIFT
							/ MAX_DISTANCE * 2);
			if (parent.getSecondCard() != null) {
				parent.getSecondCard().setScaleX(sx);
				parent.getSecondCard().setScaleY(sy);
				parent.getSecondCard().setY(
						(float) (objectY2 - distance * CARDS_SHIFT * 2
								/ MAX_DISTANCE));
			}
			if (parent.getThirdCard() != null) {
				parent.getThirdCard().setScaleX(sx);
				parent.getThirdCard().setScaleY(sy);
				parent.getThirdCard().setY(
						(float) (objectY3 - distance * CARDS_SHIFT * 2
								/ MAX_DISTANCE));
			}
			if (mFlingListener != null) {
				mFlingListener.onCardMoveDistance(distance);
			}
			break;
		case MotionEvent.ACTION_UP:
			resetCardViewOnStack();
			firstX = 0;
			firstY = 0;
			aPosX = 0;
			aPosY = 0;
			break;

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}
		}
		return true;
	}

	public double getDistance() {
		double distance = Math.sqrt((double) (Math.abs(firstCard.getX()
				- firstX)
				* Math.abs(firstCard.getX() - firstX) + Math.abs(firstCard
				.getY() - firstY)
				* Math.abs(firstCard.getY() - firstY)));
		if (distance > MAX_DISTANCE) {
			return MAX_DISTANCE;
		} else {
			return distance;
		}
	}

	private void resetCardViewOnStack() {
		if (firstCard.getX() + halfWidth < leftBorder()) {
			selectLeftExit();
		} else if (firstCard.getX() + halfWidth > rightBorder()) {
			selectRightExit();
		} else {
			float abslMoveDistance = Math.abs(firstCard.getX() - objectX)
					+ Math.abs(firstCard.getY() - objectY);
			if (abslMoveDistance == 0) {
				mFlingListener.onCardClick(dataObject);
				return;
			}
			firstCard.animate().setDuration(350)
					.setInterpolator(new OvershootInterpolator(1.0f))
					.x(objectX).y(objectY).rotation(0);
			firstCard.animate().setListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					isAnimationRunning = true;
					firstCard.post(task);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					firstCard.removeCallbacks(task);
					firstCard.setX(objectX);
					firstCard.setY(objectY);
					if (parent.getSecondCard() != null) {
						parent.getSecondCard().setScaleX(1.0f);
						parent.getSecondCard().setScaleY(1.0f);
						parent.getSecondCard().setX(objectX2);
						parent.getSecondCard().setY(objectY2);
					}
					if (parent.getThirdCard() != null) {
						parent.getThirdCard().setScaleX(1.0f);
						parent.getThirdCard().setScaleY(1.0f);
						parent.getThirdCard().setX(objectX3);
						parent.getThirdCard().setY(objectY3);
					}
					isAnimationRunning = false;
					if (mFlingListener != null) {
						mFlingListener.onCardReturn();
					}
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}
			});
		}
	}

	public float leftBorder() {
		return parentWidth / 5f;
	}

	public float rightBorder() {
		return 4 * parentWidth / 5f;
	}

	public void onSelected(final boolean isLeft, boolean isRotationed,
			long duration) {
		float exitX;
		float exitY;
		float exitRotation;
		if (isRotationed) {
			if (isLeft) {
				exitX = -objectW - getRotationWidthOffset();
				exitY = objectY;
			} else {
				exitX = parentWidth + getRotationWidthOffset();
				exitY = objectY;
			}
			exitRotation = getExitRotation(isLeft);
		} else {
			exitX = firstCard.getX() * 4;
			exitY = firstCard.getY() * 4;
			exitRotation = 0;
		}
		this.firstCard.animate().setDuration(duration)
				.setInterpolator(new LinearInterpolator()).x(exitX).y(exitY)
				.setListener(new AnimatorListenerAdapter() {

					@Override
					public void onAnimationEnd(Animator animation) {
						mFlingListener.onCardExited(isLeft);
					}
		}).rotation(exitRotation);
	}

	public void selectLeftExit() {
		if (!isAnimationRunning) {
			onSelected(true, false, 400);
		}
	}

	public void selectRightExit() {
		if (!isAnimationRunning) {
			onSelected(false, false, 400);
		}
	}

	public void rotationLeft() {
		if (!isAnimationRunning)
			onSelected(true, true, 500);
	}

	public void rotationtRight() {
		if (!isAnimationRunning)
			onSelected(false, true, 500);
	}

	private float getExitRotation(boolean isLeft) {
		float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX)
				/ parentWidth;
		if (touchPosition == TOUCH_BELOW) {
			rotation = -rotation;
		}
		if (isLeft) {
			rotation = -rotation;
		}
		return rotation;
	}

	private float getRotationWidthOffset() {
		return objectW / MAX_COS - objectW;
	}

	public void setRotationDegrees(float degrees) {
		this.BASE_ROTATION_DEGREES = degrees;
	}

	public interface FlingListener {

		public void onCardExited(boolean isLeft);

		public void onSelectLeft(double distance);

		public void onSelectRight(double distance);

		public void onCardClick(Object dataObject);

		public void onCardMoveDistance(double distance);

		public void onCardReturn();

	}

}
