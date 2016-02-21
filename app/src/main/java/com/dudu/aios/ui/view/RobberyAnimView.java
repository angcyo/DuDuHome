package com.dudu.aios.ui.view;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RobberyAnimView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "RobberyAnimView";

    private CarCheckingThread mThread;

    private OnAnimPlayListener onAnimPlayListener;

    Logger logger = LoggerFactory.getLogger(TAG);

    public RobberyAnimView(Context context) {
        super(context);
        initView(context);
    }

    public RobberyAnimView(Context context, AttributeSet attrs) {
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
                logger.debug("动画错误:", e.getMessage());
            }
            mThread = null;
        }
    }

    private class CarCheckingThread extends Thread {

        private static final int MAXIMUM_FRAME_CYCLE_COUNT = 101;

        private String PICTURE_PREFIX = "Anim_00";

        private static final String VEHICLE_CATEGORY_DIR = "animation/robbery/";

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

            doCycleAnimation();

        }

        private void doCycleAnimation() {
            while (mRunning && mFrameCounter < MAXIMUM_FRAME_CYCLE_COUNT) {
                mFrameCounter++;
                Canvas c = null;
                try {
                    synchronized (mHolder) {

                        LogUtils.v("CarCheckingView1", "当前播放帧数: " + mFrameCounter);
                        c = mHolder.lockCanvas();

                        doAnimation(c);

                        if (mFrameCounter == MAXIMUM_FRAME_CYCLE_COUNT) {
                            boolean play = onAnimPlayListener.play();
                            if (!play) {
                                mFrameCounter = 0;
                            }

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
            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Bitmap bitmap = loadAnimationBitmap();
            if (bitmap != null) {
                c.drawBitmap(bitmap, 0, 0, mPaint);
            }
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
            File file = new File(FileUtils.getStorageDir(), VEHICLE_CATEGORY_DIR + PICTURE_PREFIX + stCount + ".png");
            // is = am.open("car_checking/" + PICTURE_PREFIX + mFrameCounter + ".png");
            LogUtils.v("vehicle", "count:" + stCount);
            LogUtils.v("path", "path:" + VEHICLE_CATEGORY_DIR + PICTURE_PREFIX + stCount + ".png..." + file.exists());
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

    public void setOnAnimPlayListener(OnAnimPlayListener listener) {
        this.onAnimPlayListener = listener;
    }

    public interface OnAnimPlayListener {
        boolean play();
    }


}
