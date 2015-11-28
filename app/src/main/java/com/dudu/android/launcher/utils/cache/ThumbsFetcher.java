package com.dudu.android.launcher.utils.cache;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dudu.android.launcher.utils.LogUtils;

import java.io.FileDescriptor;

public class ThumbsFetcher extends ImageWorker {

    private static final String TAG = "ThumbsFetcher";

    private MediaMetadataRetriever retriever;

    private Context mContext;

    public ThumbsFetcher(Context context) {
        super(context);
        mContext = context;

        retriever = new MediaMetadataRetriever();
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        String path = (String) data;
        if (!TextUtils.isEmpty(path)) {
            LogUtils.v(TAG, path);
//            retriever.setDataSource(path);
//            return retriever.getFrameAtTime();
//            return getVideoThumbnail(path);
            return createVideoThumbnailBitmap(path, null, 300);
//            return getVideoThumbnail(path, 300, 300);
        }

        return null;
    }


    private Bitmap getVideoThumbnail(String path) {
        ContentResolver resolver = mContext.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,};
        String whereClause = MediaStore.Video.Media.DATA + " = '" + path + "'";

        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            do {
                _id = cursor.getInt(_idColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(resolver, _id, MediaStore.Images.Thumbnails.MINI_KIND,
                options);
        return bitmap;
    }

    private Bitmap getVideoThumbnail(String path, int width, int height) {
        Bitmap bitmap;
        bitmap = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(path,
                        MediaStore.Images.Thumbnails.MINI_KIND), width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private Bitmap createVideoThumbnailBitmap(String filePath,
                                                     FileDescriptor fd, int targetWidth) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath != null) {
                retriever.setDataSource(filePath);
            } else {
                retriever.setDataSource(fd);
            }
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (bitmap == null) return null;

        // Scale down the bitmap if it is bigger than we need.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > targetWidth) {
            float scale = (float) targetWidth / width;
            int w = Math.round(scale * width);
            int h = Math.round(scale * height);
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        return bitmap;
    }

}
