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

import com.dudu.aios.ui.voice.VoiceCircleAnimView;
import com.dudu.aios.ui.voice.VoiceRippleAnimView;
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

    private String category;

    private int model;

    private boolean isAppear = true;

    public CarCheckingView(Context context, String category, int model) {
        super(context);
        this.category = category;
        this.model = model;
        initView(context);
    }

    public CarCheckingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setIsAppear(boolean isAppear) {
        this.isAppear = isAppear;
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

        private static final int MAXIMUM_FRAME_CYCLE_COUNT = 99;

        private String picturePrefix = "NP1_0";

        private String path = "appear";

        private static final String VEHICLE_CATEGORY_DIR = "vehicle/category/";

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
            if (isAppear) {
                doAppearAnimation();
            }

            doCycleAnimation();
        }

        private void doCycleAnimation() {
            mFrameCounter = 0;
            picturePrefix = "NP2_0";
            path = "cycle";
            while (mRunning && mFrameCounter < MAXIMUM_FRAME_CYCLE_COUNT) {
                mFrameCounter++;
                if (mFrameCounter % model != 0 || mFrameCounter == MAXIMUM_FRAME_CYCLE_COUNT) {
                    Canvas c = null;
                    try {
                        synchronized (mHolder) {

                            c = mHolder.lockCanvas();

                            doAnimation(c);

                            if (mFrameCounter == MAXIMUM_FRAME_CYCLE_COUNT) {
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
        }

        private void doAppearAnimation() {
            while (mRunning && mFrameCounter < MAXIMUM_FRAME_COUNT) {
                mFrameCounter++;

                if (mFrameCounter % model != 0) {
                    Canvas c = null;
                    try {
                        synchronized (mHolder) {

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
        }

        private void doAnimation(Canvas c) {

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
            String stCount;
            if (mFrameCounter < 10) {
                stCount = "00" + mFrameCounter;
            } else if (mFrameCounter < 100) {
                stCount = "0" + mFrameCounter;
            } else {
                stCount = "" + mFrameCounter;
            }

            InputStream is;
            File file = new File(FileUtils.getAnimDir(), VEHICLE_CATEGORY_DIR + category + "/" + path + "/" + picturePrefix + stCount + ".png");

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


}
