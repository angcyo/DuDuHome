package com.dudu.android.launcher.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dudu.android.launcher.R;


public class SliderRelativeLayout extends RelativeLayout {

    private ImageView mSliderIcon = null;

    private Bitmap mDragBitmap = null;

    private Context mContext = null;

    private int mDeltaX = 0;

    private int mSliderLeft = 0;

    private ImageView mLeftRingView;

    private ImageView mRightRingView;

    private OnPhoneActionListener mListener;

    public interface OnPhoneActionListener {

        void onAcceptPhone();

        void onRejectPhone();
    }

    public SliderRelativeLayout(Context context) {
        super(context);
        mContext = context;
        initDragBitmap();
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        initDragBitmap();
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDragBitmap();
    }

    public void setOnPhoneActionListener(OnPhoneActionListener listener) {
        mListener = listener;
    }

    private void initDragBitmap() {
        mDragBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.caller_id_phone_icon);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSliderIcon = (ImageView) findViewById(R.id.slider_icon);
        mLeftRingView = (ImageView) findViewById(R.id.answer_phone_icon);
        mRightRingView = (ImageView) findViewById(R.id.reject_phone_icon);
    }

    private int mLastMoveX = 0;

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveX = (int) event.getX();
                return handleActionDownEvent(event);
            case MotionEvent.ACTION_MOVE:
                mDeltaX = x - mLastMoveX;
                mLastMoveX = x;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                handleActionUpEvent();
                return true;
        }

        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        invalidateDragImg(canvas);
    }

    private void invalidateDragImg(Canvas canvas) {
        if (mSliderIcon.getVisibility() == View.VISIBLE) {
            return;
        }

        mSliderLeft += mDeltaX;

        if (mSliderLeft < 0) {
            mSliderLeft = 0;
        } else if (mSliderLeft + mDragBitmap.getWidth() >= getWidth()) {
            mSliderLeft = getWidth() - mDragBitmap.getWidth();
        }

        int drawYCor = mSliderIcon.getTop();

        canvas.drawBitmap(mDragBitmap, mSliderLeft, drawYCor, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    private boolean handleActionDownEvent(MotionEvent event) {
        mSliderLeft = mSliderIcon.getLeft();
        Rect rect = new Rect();
        mSliderIcon.getHitRect(rect);
        boolean isHit = rect.contains((int) event.getX(), (int) event.getY());
        if (isHit) {
            mSliderIcon.setVisibility(View.INVISIBLE);
        }

        return isHit;
    }

    private void handleActionUpEvent() {

        resetViewState();

        if (mSliderLeft + mDragBitmap.getWidth() >= getWidth()
                - mRightRingView.getWidth()) {
            rejectPhone();
        } else if (mSliderLeft <= mLeftRingView.getWidth()) {
            acceptPhone();
        }
    }

    private void resetViewState() {
        mLastMoveX = 0;
        mSliderIcon.setVisibility(View.VISIBLE);
        invalidate();
    }

    private void rejectPhone() {
        Intent intent = new Intent("wld.btphone.bluetooth.CALL_REJECT");
        mContext.sendBroadcast(intent);

        if (mListener != null) {
            mListener.onRejectPhone();
        }
    }

    private void acceptPhone() {
        Intent intent = new Intent("wld.btphone.bluetooth.CALL_ACCEPT");
        mContext.sendBroadcast(intent);

        if (mListener != null) {
            mListener.onAcceptPhone();
        }
    }
}
