package com.dudu.aios.ui.voice;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxh on 2016/2/13.
 */
public abstract class VoiceAnimView extends SurfaceView implements SurfaceHolder.Callback {


    private int maxPicCount = 0;

    private String picPath = "";

    private VoiceAnimThread voiceAnimThread;

    private static final String PICTURE_DIR = "animation/voice/";

    public VoiceAnimView(Context context) {
        super(context);
        initView(context);
    }

    public VoiceAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startAnim();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        voiceAnimThread.setRunning(false);

        try {
            voiceAnimThread.join();
        } catch (Exception e) {

        }
    }

    public void startAnim() {
        voiceAnimThread.setRunning(true);
        voiceAnimThread.start();
    }

    private void initView(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        voiceAnimThread = new VoiceAnimThread(context, holder);
        picPath = getPicPath();
        maxPicCount = getMaxPicCount();

        setFocusable(true);
    }

    protected abstract int getMaxPicCount();

    protected abstract String getPicPath();

    public void stopAnim() {
        voiceAnimThread.setRunning(false);
    }

    class VoiceAnimThread extends Thread {


        private Context mContext;

        private boolean mRunning;

        private SurfaceHolder mHolder;

        private Paint mPaint;

        private int mFrameCounter = 0;

        public VoiceAnimThread(Context context, SurfaceHolder holder) {
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
            while (mRunning) {
                Canvas c = null;
                try {
                    synchronized (mHolder) {

                        if (mFrameCounter == maxPicCount) {
                            mFrameCounter = 0;
                        }

                        mFrameCounter++;

                        c = mHolder.lockCanvas();
                        doAnimation(c);

                        Thread.sleep(30);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

            //AssetManager am = mContext.getAssets();

            File file = new File(FileUtils.getStorageDir(), PICTURE_DIR + picPath + mFrameCounter + ".png");
            if (file.exists()) {
                InputStream is;
                try {
                    // is = am.open(picPath + mFrameCounter + ".png");
                    is = new FileInputStream(file);
                    return BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
