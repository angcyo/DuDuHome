package com.dudu.android.launcher.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.dudu.android.launcher.R;

/**
 * SlideSwitch 仿iphone滑动开关组件，仿百度魔图滑动开关组件 组件分为三种状态：打开、关闭、正在滑动<br/>
 * 用到三张图， 使用方法：
 * 
 * <pre>
 * SlideSwitch slideSwitch = new SlideSwitch(this);
 * slideSwitch.setOnSwitchChangedListener(onSwitchChangedListener);
 * linearLayout.addView(slideSwitch);
 * </pre>
 * 
 * 注：也可以加载在xml里面使用
 */
@SuppressLint("DrawAllocation")
public class SlideSwitch extends View {
	public static final String TAG = "SlideSwitch";
	public static final int SWITCH_OFF = 0;// 关闭状态
	public static final int SWITCH_ON = 1;// 打开状态
	public static final int SWITCH_SCROLING = 2;// 滚动状态
	/**
	 * 开关的状态（默认为关闭）
	 */
	private int mSwitchStatus = SWITCH_OFF;
	/**
	 * 表示是否发生过滚动
	 */
	private boolean mHasScrolled = false;
	/**
	 * 不动部分图片的宽度
	 */
	private int mBmpWidth = 0;
	/**
	 * 不动部分图片的高度
	 */
	private int mBmpHeight = 0;
	/**
	 * 滚动部分的图片的宽度
	 */
	private int mThumbWidth = 0;
	/**
	 * 自己的画笔
	 */
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	/**
	 * 开关所需要的三张图片
	 */
	Bitmap mSwitch_off, mSwitch_on, mSwitch_thumb;
	/**
	 * 默认起始点和终止点
	 */
	private int mSrcX = 0, mDstX = 0;
	/**
	 * 是否显示开关的汉字
	 */
	private boolean isShow = true;
	// 用于显示的文本，如果项目不需要，可以直接注释掉
	private String mOnText = "关";
	private String mOffText = "开";
	private OnSwitchChangedListener mOnSwitchChangedListener = null;

	public SlideSwitch(Context context) {
		this(context, null);
	}

	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// 初始化三幅图片，自己要改的地方
	private void setBitmap() {
		Resources res = getResources();
		mSwitch_off = BitmapFactory.decodeResource(res,
				R.drawable.switch_bottom);
		mSwitch_on = BitmapFactory
				.decodeResource(res, R.drawable.switch_bottom);
		mSwitch_thumb = BitmapFactory.decodeResource(res,
				R.drawable.switch_button);
	}

	private void init() {
		setBitmap();
		mBmpWidth = mSwitch_on.getWidth();
		mBmpHeight = mSwitch_on.getHeight();
		mThumbWidth = mSwitch_thumb.getWidth();
	}

	/**
	 * 对组件的适配
	 */
	@Override
	public void setLayoutParams(LayoutParams params) {
		if (params.height < 0 || params.width < 0) {
			params.width = mBmpWidth;
			params.height = mBmpHeight;
		} else {
			mSwitch_off = PictureZoom(mSwitch_off, params.width, params.height);
			mSwitch_on = PictureZoom(mSwitch_on, params.width, params.height);
			mSwitch_thumb = PictureZoom(mSwitch_thumb, params.height,
					params.height);
			mBmpWidth = mSwitch_off.getWidth();
			mBmpHeight = mSwitch_off.getHeight();
			mThumbWidth = mSwitch_thumb.getWidth();
			params.width = mBmpWidth;
			params.height = mBmpHeight;
		}
		super.setLayoutParams(params);
	}

	/**
	 * 将图片进行缩放
	 * 
	 * @param bitmap
	 *            需要缩放的图片
	 * @param newWidth
	 *            想要的图片的新的宽度
	 * @param newHeight
	 *            想要的图片的新的高度
	 */
	private Bitmap PictureZoom(Bitmap bitmap, int newWidth, int newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return bitmap;
	}

	/**
	 * 为开关控件设置状态改变监听函数
	 * 
	 * @param onSwitchChangedListener
	 *            参见 {@link OnSwitchChangedListener}
	 */
	public void setOnSwitchChangedListener(
			OnSwitchChangedListener onSwitchChangedListener) {
		mOnSwitchChangedListener = onSwitchChangedListener;
	}

	/**
	 * 设置开关上面的文本
	 * 
	 * @param onText
	 *            控件打开时要显示的文本
	 * @param offText
	 *            控件关闭时要显示的文本
	 */
	public void setText(final String onText, final String offText) {
		mOnText = onText;
		mOffText = offText;
		invalidate();
	}

	/**
	 * 设置开关上面的文本
	 * 
	 * @param onText
	 *            控件打开时要显示的文本
	 * @param offText
	 *            控件关闭时要显示的文本
	 * @param isShow
	 *            是否显示
	 */
	public void setText(final String onText, final String offText,
			boolean isShow) {
		this.isShow = isShow;
		setText(onText, offText);
	}

	/**
	 * 是否显示开关上的字
	 * 
	 * @param isShow
	 */
	public void setTextIsShow(boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * 设置开关的状态
	 * 
	 * @param on
	 *            是否打开开关 打开为true 关闭为false
	 */
	public void setStatus(boolean on) {
		mSwitchStatus = (on ? SWITCH_ON : SWITCH_OFF);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		// 记录下按下时的x坐标
		case MotionEvent.ACTION_DOWN:
			mSrcX = (int) event.getX();
			break;
		// 计算滑动时x轴y轴的移动变化
		case MotionEvent.ACTION_MOVE:
			mDstX = Math.max((int) event.getX(), mThumbWidth / 2);
			mDstX = Math.min(mDstX, mBmpWidth - mThumbWidth / 2);
			if (mSrcX == mDstX)
				return true;
			mHasScrolled = true;
			mSwitchStatus = SWITCH_SCROLING;
			AnimationTransRunnable aTransRunnable = new AnimationTransRunnable(
					mSrcX, mDstX, 0);
			new Thread(aTransRunnable).start();
			mSrcX = mDstX;
			break;
		// 记录下抬起时的x轴y轴坐标
		case MotionEvent.ACTION_UP:
			if (mHasScrolled == false) {
				// 如果没有发生过滑动，就意味着这是一次单击过程
				mSwitchStatus = Math.abs(mSwitchStatus - 1);
				int xFrom = mThumbWidth / 2, xTo = mBmpWidth - mThumbWidth / 2;
				if (mSwitchStatus == SWITCH_OFF) {
					xFrom = mBmpWidth - mThumbWidth / 2;
					xTo = mThumbWidth / 2;
				}
				AnimationTransRunnable runnable = new AnimationTransRunnable(
						xFrom, xTo, 1);
				new Thread(runnable).start();
			} else {
				// 刷新界面，可以网上搜索一下invalidate与postInvalidate刷新界面的区别（下面有用到），这里就不再讲解了
				invalidate();
				mHasScrolled = false;
			}
			// 状态改变的时候 回调事件函数
			if (mOnSwitchChangedListener != null) {
				mOnSwitchChangedListener.onSwitchChanged(this, mSwitchStatus);
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 设置显示字体大小
		mPaint.setTextSize(mThumbWidth / 3);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		if (mSwitchStatus == SWITCH_OFF) {
			drawBitmap(canvas, null, null, mSwitch_off);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			mPaint.setColor(Color.WHITE);
			canvas.translate(mThumbWidth, 0);
			setDrawText(canvas, mOffText, isShow);
		} else if (mSwitchStatus == SWITCH_ON) {
			drawBitmap(canvas, null, null, mSwitch_on);
			int count = canvas.save();
			canvas.translate(mBmpWidth - mThumbWidth, 0);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			mPaint.setColor(Color.WHITE);
			canvas.restoreToCount(count);
			setDrawText(canvas, mOnText, isShow);
		} else {
			// 正在滑动中
			mSwitchStatus = mDstX == mBmpWidth - mThumbWidth / 2 ? SWITCH_ON
					: SWITCH_OFF;
			drawBitmap(canvas, new Rect(0, 0, mDstX, mBmpHeight), new Rect(0,
					0, (int) mDstX, mBmpHeight), mSwitch_on);
			mPaint.setColor(Color.WHITE);
			setDrawText(canvas, mOnText, isShow);
			int count = canvas.save();
			canvas.translate(mDstX, 0);
			drawBitmap(canvas, new Rect(mDstX, 0, mBmpWidth, mBmpHeight),
					new Rect(0, 0, mBmpWidth - mDstX, mBmpHeight), mSwitch_off);
			canvas.restoreToCount(count);
			count = canvas.save();
			canvas.clipRect(mDstX, 0, mBmpWidth, mBmpHeight);
			canvas.translate(mThumbWidth, 0);
			mPaint.setColor(Color.WHITE);
			setDrawText(canvas, mOffText, isShow);
			canvas.restoreToCount(count);
			count = canvas.save();
			canvas.translate(mDstX - mThumbWidth / 2, 0);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			canvas.restoreToCount(count);
		}

	}

	/**
	 * 画出text内容
	 * 
	 * @param canvas
	 *            需要绘制的画布
	 * @param text
	 *            需要的text
	 * @param isShow
	 *            是否需要画制
	 */
	private void setDrawText(Canvas canvas, String text, boolean isShow) {
		if (isShow) {
			canvas.drawText(text, mThumbWidth / 4, mThumbWidth * 2 / 5, mPaint);
		}
	}

	public void drawBitmap(Canvas canvas, Rect src, Rect dst, Bitmap bitmap) {
		dst = (dst == null ? new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight()) : dst);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, src, dst, paint);
	}

	/**
	 * AnimationTransRunnable 做滑动动画所使用的线程
	 */
	private class AnimationTransRunnable implements Runnable {
		private int srcX, dstX;
		private int duration;

		/**
		 * 滑动动画
		 * 
		 * @param srcX
		 *            滑动起始点
		 * @param dstX
		 *            滑动终止点
		 * @param duration
		 *            是否采用动画，1采用，0不采用
		 */
		public AnimationTransRunnable(float srcX, float dstX, final int duration) {
			this.srcX = (int) srcX;
			this.dstX = (int) dstX;
			this.duration = duration;
		}

		@Override
		public void run() {
			final int patch = (dstX > srcX ? 5 : -5);
			if (duration == 0) {
				SlideSwitch.this.mSwitchStatus = SWITCH_SCROLING;
				// postInvalidate刷新界面
				SlideSwitch.this.postInvalidate();
			} else {
				int x = srcX + patch;
				while (Math.abs(x - dstX) > 5) {
					mDstX = x;
					SlideSwitch.this.mSwitchStatus = SWITCH_SCROLING;
					SlideSwitch.this.postInvalidate();
					x += patch;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mDstX = dstX;
				SlideSwitch.this.mSwitchStatus = mDstX > mSwitch_off.getWidth() / 2 ? SWITCH_ON
						: SWITCH_OFF;
				SlideSwitch.this.postInvalidate();
			}
		}
	}

	public static interface OnSwitchChangedListener {
		/**
		 * 状态改变 回调函数
		 * 
		 * @param status
		 *            SWITCH_ON表示打开 SWITCH_OFF表示关闭
		 */
		public abstract void onSwitchChanged(SlideSwitch obj, int status);
	}
}