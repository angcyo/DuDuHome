package com.dudu.aios.ui.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenUtil {



    public static Bitmap takeScreenShot(Activity activity) {

        View view = activity.getWindow().getDecorView();  
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap b1 = view.getDrawingCache();  

        // 获取屏幕长和高  
        int width = activity.getResources().getDisplayMetrics().widthPixels;  
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        // 去掉标题栏  
        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();  
        
        return b; 
    }


    public static Bitmap cacheCurrentScreen(Activity activity) {

        Bitmap currentBitmap = null;

        View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        root.setDrawingCacheEnabled(true);
        Bitmap drawingCache = root.getDrawingCache();

        currentBitmap = Bitmap.createBitmap(drawingCache.getWidth(), drawingCache.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(currentBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(drawingCache, 0, 0, paint);

        root.destroyDrawingCache();

        return currentBitmap;
    }

}
