package com.dudu.aios.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dudu.android.launcher.R;

import org.scf4a.EventWrite;

public class FlowCompletedView extends View {
    //画实心圆的画笔
    private Paint mCirclePaint;
    //画圆环的画笔
    private Paint mRingPaint;
    //话圆环2的画笔
    private Paint mRingPaint2;
    //圆形的颜色
    private int mCircleColor;
    //圆环1的颜色
    private int mRingColor;
    //圆环2的颜色
    private int mRingColor2;
    //圆的半径
    private float mRadius;
    //圆环的半径
    private float mRingRadius;
    //圆环的宽度
    private float mStrokeWidth;
    //圆心轴X的坐标
    private int mXCenter;
    //圆心Y轴的坐标
    private int mYCenter;
    //总进度
    private int mTotalProgress = 100;
    //当前的进度
    private int mProgress;


    public FlowCompletedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获得自定义的属性
        initAttrs(context, attrs);
        //初始化参数
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlowCompletedView, 0, 0);
        mRadius = array.getDimension(R.styleable.FlowCompletedView_mRadius, 140);
        mStrokeWidth = array.getDimensionPixelSize(R.styleable.FlowCompletedView_mStrokeWidth, 18);
        mCircleColor = array.getColor(R.styleable.FlowCompletedView_mCircleColor, 0x00000000);
        mRingColor = array.getColor(R.styleable.FlowCompletedView_mRingColor, getResources().getColor(R.color.chat_list_item_background_color));
        mRingColor2 = array.getColor(R.styleable.FlowCompletedView_mRingColor, getResources().getColor(R.color.color_6b));
        mRingRadius = mRadius + mStrokeWidth / 2;
    }

    private void initVariable() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);//实心

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);//消除锯齿
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);//空心
        mRingPaint.setStrokeWidth(mStrokeWidth);

        mRingPaint2 = new Paint();
        mRingPaint2.setAntiAlias(true);
        mRingPaint2.setColor(mRingColor2);
        mRingPaint2.setStyle(Paint.Style.STROKE);
        mRingPaint2.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;

        //画圆(圆心，半径，圆的画笔)
        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

        //画阴影的圆环(圆弧的外轮廓矩形区域,绘制扇形，画笔)
        RectF oval = new RectF();
        oval.left = mXCenter - mRingRadius;
        oval.top = mYCenter - mRingRadius;
        oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
        oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
        canvas.drawArc(oval, -90, 1 * 360, false, mRingPaint2);

        if (mProgress > 0) {
            RectF oval2 = new RectF();
            oval2.left = mXCenter - mRingRadius;
            oval2.top = mYCenter - mRingRadius;
            oval2.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval2.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval2, -90, ((float) mProgress / mTotalProgress) * 360, false, mRingPaint);
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }
}
