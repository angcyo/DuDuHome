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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;

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

    private static final String PICTURE_DIR = "voice/";
    private Context context;


    public VoiceAnimView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public VoiceAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopAnim();
    }

    public void startAnim() {

        if (voiceAnimThread == null) {
            voiceAnimThread = new VoiceAnimThread(context, getHolder());
            voiceAnimThread.setRunning(true);
            voiceAnimThread.start();
        } else {
            voiceAnimThread.setRunning(true);
        }
    }

    private void initView(

    ) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        picPath = getPicPath();
        maxPicCount = getMaxPicCount();

        setFocusable(true);
    }

    protected abstract int getMaxPicCount();

    protected abstract String getPicPath();

    public void stopAnim() {
        if (voiceAnimThread != null) {
            Log.d("lxh", "-------voice anim stop");
            voiceAnimThread.setRunning(false);
            voiceAnimThread = null;
        }
    }

    class VoiceAnimThread extends Thread {


        private Context mContext;

        public boolean ismRunning() {
            return mRunning;
        }

        private boolean mRunning = false;

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
                        try {
                            mHolder.unlockCanvasAndPost(c);
                        } catch (Exception e) {

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
            String path = "";
            if (picPath.equals(VoiceRippleAnimView.VOICE_RIPPLE_PATH)) {
                path = "d02_voice_1";
            } else if (picPath.equals(VoiceCircleAnimView.VOICE_CIRCLE_PATH)) {
                path = "voice_circle_1";
            }
            try {
                is = am.open("animation/" + path + ".png");
            } catch (IOException e) {
                return null;
            }
            return BitmapFactory.decodeStream(is);
        }

        private Bitmap loadAnimationBitmap() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;

            //AssetManager am = mContext.getAssets();

            File file = new File(FileUtils.getAnimDir(), PICTURE_DIR + picPath + mFrameCounter + ".png");
//            LogUtils.v("path", "path:" + file.getPath() + "---" + file.exists());
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
