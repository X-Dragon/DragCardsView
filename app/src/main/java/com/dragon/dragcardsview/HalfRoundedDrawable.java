package com.dragon.dragcardsview;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class HalfRoundedDrawable extends Drawable {
	protected final float cornerRadius;
	protected final int margin;
	protected final RectF mRect = new RectF();
	protected final RectF mBitmapRect;
	protected RectF mRect2 = new RectF();
	protected RectF mRect3 = new RectF();
	protected final BitmapShader bitmapShader;
	protected final Paint paint;

	public HalfRoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
		this.cornerRadius = cornerRadius;
		this.margin = margin;
		this.bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		this.mBitmapRect = new RectF(margin, margin,
				bitmap.getWidth() - margin, bitmap.getHeight() - margin);
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setShader(this.bitmapShader);
		this.paint.setFilterBitmap(true);
		this.paint.setDither(true);
	}

	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
	}

	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		this.mRect.set(this.margin, this.margin, bounds.width() - this.margin,
				bounds.height() - this.margin);
		this.mRect2.set(this.margin, this.margin, bounds.width() - this.margin,
				bounds.height() - this.margin - this.cornerRadius);
		this.mRect3.set(this.margin, bounds.height() - this.margin
				- this.cornerRadius, bounds.width() - this.margin,
				bounds.height() - this.margin);

		Matrix shaderMatrix = new Matrix();
		shaderMatrix.setRectToRect(this.mBitmapRect, this.mRect,
				Matrix.ScaleToFit.FILL);
		this.bitmapShader.setLocalMatrix(shaderMatrix);
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.clipRect(this.mRect2);
		canvas.drawRoundRect(this.mRect, this.cornerRadius, this.cornerRadius,
				this.paint);
		canvas.restore();
		canvas.drawRect(this.mRect3, this.paint);
	}

	public int getOpacity() {
		return -3;
	}

	public void setAlpha(int alpha) {
		this.paint.setAlpha(alpha);
	}

	public void setColorFilter(ColorFilter cf) {
		this.paint.setColorFilter(cf);
	}
}