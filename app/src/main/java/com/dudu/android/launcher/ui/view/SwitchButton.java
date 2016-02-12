package com.dudu.android.launcher.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dudu.android.launcher.R;

public class SwitchButton extends View implements android.view.View.OnTouchListener {
    private Bitmap mSwitchBackground, mOnSlipper;
    /**
     * 按下时的x和当前的x
     */
    private float downX, nowX;

    /**
     * 记录用户是否在滑动
     */
    private boolean onSlip = false;

    /**
     * 当前的状态
     */
    private boolean nowStatus = false;

    /**
     * 监听接口
     */
    private OnChangedListener listener;


    public SwitchButton(Context context) {
        super(context);
        init();
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        //载入图片资源
        mSwitchBackground = BitmapFactory.decodeResource(getResources(), R.drawable.vehicle_robbery_bg);
//        mOffSlipper = BitmapFactory.decodeResource(getResources(), R.drawable.vehicle_robbery_off);
        mOnSlipper = BitmapFactory.decodeResource(getResources(), R.drawable.vehicle_robbery_on);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x = 0;
        canvas.drawBitmap(mSwitchBackground, matrix, paint);//画出背景

        if (onSlip) {//是否是在滑动状态,
            if (nowX >= mSwitchBackground.getWidth())//是否划出指定范围,不能让滑块跑到外头,必须做这个判断
                x = mSwitchBackground.getWidth() - mOnSlipper.getWidth() / 2;//减去滑块1/2的长度
            else
                x = nowX - mOnSlipper.getWidth() / 2;
        } else {
            if (nowStatus) {//根据当前的状态设置滑块的x值
                x = mSwitchBackground.getWidth() - mOnSlipper.getWidth();
            } else {
                x = 0;
            }
        }

        //对滑块滑动进行异常处理，不能让滑块出界
        if (x < 0) {
            x = 0;
        } else if (x > mSwitchBackground.getWidth() - mOnSlipper.getWidth()) {
            x = mSwitchBackground.getWidth() - mOnSlipper.getWidth();
        }
        //根据nowX设置背景，开或者关状态
//        if (nowX < (mSwitchBackground.getWidth() / 2)) {
//        canvas.drawBitmap(mOffSlipper, x, 0, paint);//关闭时
//        } else {
        canvas.drawBitmap(mOnSlipper, x, 0, paint);//打开时
//        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = mSwitchBackground.getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mSwitchBackground.getHeight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (event.getX() > mSwitchBackground.getWidth() || event.getY() > mSwitchBackground.getHeight()) {
                    return false;
                } else {
                    onSlip = true;
                    downX = event.getX();
                    nowX = downX;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                nowX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                onSlip = false;
                if (event.getX() >= (mSwitchBackground.getWidth() / 2)) {
                    nowStatus = true;
                    nowX = mSwitchBackground.getWidth() - mOnSlipper.getWidth();
                } else {
                    nowStatus = false;
                    nowX = 0;
                }

                if (listener != null) {
                    listener.OnChanged(SwitchButton.this, nowStatus);
                }
                break;
            }
        }
        //刷新界面
        invalidate();
        return true;
    }


    /**
     * 为WiperSwitch设置一个监听，供外部调用的方法
     *
     * @param listener
     */
    public void setOnChangedListener(OnChangedListener listener) {
        this.listener = listener;
    }


    /**
     * 设置滑动开关的初始状态，供外部调用
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        if (checked) {
            nowX = mSwitchBackground.getWidth();
        } else {
            nowX = 0;
        }
        nowStatus = checked;
    }


    /**
     * 回调接口
     *
     * @author len
     */
    public interface OnChangedListener {
        public void OnChanged(SwitchButton wiperSwitch, boolean checkState);
    }


}