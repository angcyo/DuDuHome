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

public class VehicleCheckResultAnimation extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CarCheckingView";

    private CarCheckingThread mThread;

    private String category;

    public VehicleCheckResultAnimation(Context context, String category) {
        super(context);
        this.category = category;
        initView(context);
    }

    public VehicleCheckResultAnimation(Context context, AttributeSet attrs) {
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

    private class CarCheckingThread extends Thread {

        private static final int MAXIMUM_FRAME_COUNT = 50;

        private int maxCycleCount = 148;

        private Context mContext;

        private boolean mRunning;

        private SurfaceHolder mHolder;

        private Paint mPaint;

        private int mFrameCounter = 0;

        private static final String VEHICLE_MALFUNCTION = "vehicle/malfunction/";

        private String path = "appear";

        private static final String PICTURE_FRAME_PREFIX = "Anim_00";


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

            doAppearAnimation();

            doCycleAnimation();

        }

        private void doAppearAnimation() {
            while (mRunning && mFrameCounter < MAXIMUM_FRAME_COUNT) {
                Canvas c = null;
                try {
                    synchronized (mHolder) {
                        mFrameCounter++;

//                        LogUtils.v("CarCheckingView", "当前播放帧数: " + mFrameCounter);
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

        private void doCycleAnimation() {
            mFrameCounter = 0;
            path = "cycle";
            File file = new File(FileUtils.getAnimDir(), VEHICLE_MALFUNCTION + category + "/" + path);
            if (file.exists() && file.isDirectory()) {
                maxCycleCount = file.listFiles().length;
            }
            while (mRunning && mFrameCounter < maxCycleCount - 1) {
                Canvas c = null;
                try {
                    synchronized (mHolder) {
                        mFrameCounter++;

//                        LogUtils.v("CarCheckingView", "当前播放帧数: " + mFrameCounter);
                        c = mHolder.lockCanvas();

                        doAnimation(c);
                        if (mFrameCounter == maxCycleCount - 1) {
                            mFrameCounter = 0;
                        }
                    }
                } finally {
                    if (c != null) {
                        mHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void doAnimation(Canvas c) {

            try {

                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                Bitmap bitmap = loadAnimationBitmap();
                if (bitmap != null) {
                    c.drawBitmap(bitmap, 0, 0, mPaint);
                } else {

                    Bitmap b = loadStaticBitmap();
                    if (b != null) {
                        c.drawBitmap(b, 0, 0, mPaint);

                    }
                }
            } catch (Exception e) {

            }


        }

        private Bitmap loadStaticBitmap() {
            AssetManager am = mContext.getAssets();
            InputStream is;
            LogUtils.v("vehicle", "静态的");
            try {
                is = am.open("animation/" + category + "_NP1.png");
            } catch (IOException e) {
                return null;
            }
            return BitmapFactory.decodeStream(is);
        }

        private Bitmap loadAnimationBitmap() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;

            // AssetManager am = mContext.getAssets();
            String frameCount;

            if (mFrameCounter < 10) {
                frameCount = "00" + mFrameCounter;
            } else if (mFrameCounter < 100) {
                frameCount = "0" + mFrameCounter;
            } else {
                frameCount = "" + mFrameCounter;
            }

            LogUtils.v("vehicle", frameCount);
            InputStream is;
            File file = new File(FileUtils.getAnimDir(), VEHICLE_MALFUNCTION + category + "/" + path + "/" + PICTURE_FRAME_PREFIX + frameCount + ".png");
            // is = am.open("car_checking/" + PICTURE_PREFIX + mFrameCounter + ".png");
            LogUtils.v("path", "path" + file.getPath() + "---" + file.exists());
            if (file.exists()) {
                try {
                    is = new FileInputStream(file);
                    return BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    LogUtils.e("CarCheckingView", e.getMessage());
                    return null;
                }
            }
            return null;

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
}
