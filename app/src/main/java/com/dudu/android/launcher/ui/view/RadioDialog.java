package com.dudu.android.launcher.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.dudu.android.launcher.R;

@SuppressLint("Recycle")
public class RadioDialog extends View {

	/**
	 * 圆圈个数
	 */
	private int pressCounts = 0;

	private Drawable centerDrawable, circleDrawable;
	/**
	 * 绘图的Paint
	 */
	private Paint mBitmapPaint;

	public RadioDialog(Context context) {
		this(context, null);
	}

	public RadioDialog(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 必要的初始化，获得一些自定义的值
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public RadioDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray tArray = context.obtainStyledAttributes(attrs,
				R.styleable.SemanticDialog, defStyle, 0);

		circleDrawable = tArray
				.getDrawable(R.styleable.SemanticDialog_circleDrawable);

		tArray.recycle();

		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (pressCounts < 4) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_0);
		} else if (pressCounts < 5) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_1);
		} else if (pressCounts < 7 && pressCounts >= 5) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_2);
		} else if (pressCounts < 10 && pressCounts >= 7) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_3);
		} else if (pressCounts < 13 && pressCounts >= 10) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_4);
		} else if (pressCounts < 16 && pressCounts >= 13) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_5);
		} else if (pressCounts < 19 && pressCounts >= 16) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_6);
		} else if (pressCounts < 22 && pressCounts >= 19) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_7);
		} else if (pressCounts < 25 && pressCounts >= 22) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_8);
		} else if (pressCounts < 30 && pressCounts >= 25) {
			circleDrawable = getResources().getDrawable(R.drawable.circle_9);
		} else {
			circleDrawable = getResources().getDrawable(R.drawable.circle_10);
		}

		Bitmap circleBitmap = drawableToBitamp(circleDrawable);

		centerDrawable = getResources().getDrawable(R.drawable.circle_drama);
		Bitmap centerBitmap = drawableToBitamp(centerDrawable);
		canvas.drawBitmap(centerBitmap,
				getWidth() * 2 / 10 - centerBitmap.getWidth() / 2, getHeight()
						/ 2 - centerBitmap.getHeight() / 2, mBitmapPaint);
		canvas.drawBitmap(circleBitmap,
				getWidth() * 2 / 10 - circleBitmap.getWidth() / 2, getHeight()
						/ 2 - circleBitmap.getHeight() / 2, mBitmapPaint);
	}

	/**
	 * drawable转bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitamp(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable dBitmapDrawable = (BitmapDrawable) drawable;
			return dBitmapDrawable.getBitmap();
		}

		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	public void setPressCounts(int pressCounts) {
		this.pressCounts = pressCounts;
		invalidate();
	}
}