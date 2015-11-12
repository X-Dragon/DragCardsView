package com.dragon.dragcardsview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class AntiAliasView extends LinearLayout {
	public AntiAliasView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AntiAliasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.e("AntiAliasView", "AntiAliasView");
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public AntiAliasView(Context context, AttributeSet attrs, int defStyleAttr) {
		// TODO Auto-generated constructor stub
		super(context, attrs, defStyleAttr);
		Log.e("AntiAliasView", "AntiAliasView");
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		Log.e("AntiAliasView", "dispatchDraw");
	}
	
}
