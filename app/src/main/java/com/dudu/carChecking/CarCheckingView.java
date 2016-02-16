package com.dudu.carChecking;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/2/2.
 */
public class CarCheckingView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CarCheckingView";

    private CarCheckingThread mThread;

    public CarCheckingView(Context context) {
        super(context);
        initView(context);
    }

    public CarCheckingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startAnim();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.e(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopAnim();
    }

    public void startAnim() {
        if (mThread == null) {
            mThread = new CarCheckingThread(getContext(), getHolder());
            mThread.setRunning(true);
            mThread.start();
        } else {
            mThread.setRunning(true);
        }
    }

    public void stopAnim() {
        if (mThread != null) {
            mThread.setRunning(false);
            try {
                mThread.join();
            } catch (InterruptedException e) {
                LogUtils.e("CarCheckingView", e.getMessage());
            }
            mThread = null;
        }
    }

    private class CarCheckingThread extends Thread {

        private static final int MAXIMUM_FRAME_COUNT = 125;

        private static final String PICTURE_PREFIX = "normal_p1_car";

        private Context mContext;

        private boolean mRunning;

        private SurfaceHolder mHolder;

        private Paint mPaint;

        private int mFrameCounter = 0;

        public CarCheckingThread(Context context, SurfaceHolder holder) {
            mContext = context;
            mHolder = holder;
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        public void setRunning(boolean running) {
            mRunning = running;
        }

        @Override
        public void run() {
            while (mRunning && mFrameCounter < MAXIMUM_FRAME_COUNT) {
                Canvas c = null;
                try {
                    synchronized (mHolder) {
                        mFrameCounter++;

                        LogUtils.v("CarCheckingView", "当前播放帧数: " + mFrameCounter);
                        c = mHolder.lockCanvas();

                        doAnimation(c);
                    }
                } finally {
                    if (c != null) {
                        mHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void doAnimation(Canvas c) {

            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (loadAnimationBitmap() != null) {
                c.drawBitmap(loadAnimationBitmap(), 0, 0, mPaint);
            }

        }

        private Bitmap loadAnimationBitmap() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;

            // AssetManager am = mContext.getAssets();

            InputStream is;
            File file = new File(FileUtils.getStorageDir(), "animation/vehicle/car_checking/" + PICTURE_PREFIX + mFrameCounter + ".png");
            // is = am.open("car_checking/" + PICTURE_PREFIX + mFrameCounter + ".png");
            LogUtils.v("vehicle", FileUtils.getStorageDir().toString());
            if (file.exists()) {
                try {
                    LogUtils.v("vehicle", "存在");
                    is = new FileInputStream(file);
                    return BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    LogUtils.e("CarCheckingView", e.getMessage());
                    return null;
                }
            } else {
                LogUtils.v("vehicle", "不存在");
            }

            return null;

        }
    }


}
