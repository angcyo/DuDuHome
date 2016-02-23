package com.dudu.aios.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;

public class VehicleCheckResultView extends View {
    /* drawLine(float startX, float startY, float stopX, float stopY, Paintpaint)
    //画线，参数一起始点的x轴位置，参数二起始点的y轴位置，参数三终点的x轴水平位置，参数四y轴垂直位置，最后一个参数为Paint 画刷对象。*/

    private Paint paintBg;

    private Paint paint;

    private int colorBg;

    private int color;

    private float width = 0;

    private float widthBg = 580;

    private Context context;

    private AttributeSet attrs;

    public VehicleCheckResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VehicleLine);
        colorBg = array.getColor(R.styleable.VehicleLine_VehicleLineColor, getResources().getColor(R.color.transparent));
        color = array.getColor(R.styleable.VehicleLine_VehicleLineColor, getResources().getColor(R.color.blue));
//        LogUtils.v("vehicle1","1");
        array.recycle();
        initView();
    }

    private void initView() {
        paintBg = new Paint();
        paintBg.setStyle(Paint.Style.FILL);
        paintBg.setAntiAlias(true);//消除锯齿
        paintBg.setColor(colorBg);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//消除锯齿
        paint.setColor(color);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       /* //画背景的直线
        canvas.drawLine(0, 0, widthBg, 0, paintBg);
        //话渲染上的直线
        canvas.drawLine(0, 0, width, 0, paint);*/

        RectF oval = new RectF(0, 0, widthBg, 20);// 设置个新的长方形
        canvas.drawRoundRect(oval, 10, 10, paintBg);//第二个参数是x半径，第三个参数是y半径
        RectF oval1 = new RectF(0, 0, width, 20);// 设置个新的长方形
        canvas.drawRoundRect(oval1, 10, 10, paint);//第二个参数是x半径，第三个参数是y半径
    }

    public void setProgress(int progress, int state) {
        width = ((float) progress / 100) * widthBg;
        if (state == 1) {
            paint.setColor(getResources().getColor(R.color.red));

        }
        postInvalidate();
    }
}
