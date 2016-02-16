package com.dudu.carChecking;

import android.content.Context;
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

    private String frameCount = "1";

    private static final String PICTURE_PREFIX_ONE = "Warning_P2_";

    private static final String PICTURE_PREFIX_TWO = "_";

    private static final String PICTURE_PREFIX_THREE = "_scale_0";

    private static final String PICTURE_DIR = "animation/vehicle/";

    private String picturePath;

    public VehicleCheckResultAnimation(Context context, String path, String state) {
        super(context);
        String picturePrefix = "engine";
        if (path.equals("gearbox")) {
            picturePrefix = "Transsision";
        } else if (path.equals("engine")) {
            picturePrefix = "engine";
        } else if (path.equals("abs")) {
            picturePrefix = "abs";
        } else if (path.equals("wsb")) {
            picturePrefix = "tire";
        } else if (path.equals("srs")) {
            picturePrefix = "SRS";
        }
        picturePath = PICTURE_DIR + path + "/" + state + "/" + PICTURE_PREFIX_ONE + picturePrefix + PICTURE_PREFIX_TWO + state + PICTURE_PREFIX_THREE;
        LogUtils.v("jjj", picturePath);
        initView(context);
    }

    public VehicleCheckResultAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mThread = new CarCheckingThread(context, holder);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.e(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.setRunning(false);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            LogUtils.e("CarCheckingView", e.getMessage());
        }
    }

    private class CarCheckingThread extends Thread {

        private static final int MAXIMUM_FRAME_COUNT = 50;


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

            if (mFrameCounter < 10) {
                frameCount = "00" + mFrameCounter;
            } else if (mFrameCounter < 100) {
                frameCount = "0" + mFrameCounter;
            }else {
                frameCount=""+mFrameCounter;
            }

            LogUtils.v("vehicle", frameCount);

            InputStream is;
            File file = new File(FileUtils.getStorageDir(), picturePath + frameCount + ".png");
            // is = am.open("car_checking/" + PICTURE_PREFIX + mFrameCounter + ".png");
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

    public void stopAnim(){
        mThread.setRunning(false);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            LogUtils.e("CarCheckingView", e.getMessage());
        }
    }
}
