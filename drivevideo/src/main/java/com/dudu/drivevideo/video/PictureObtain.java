package com.dudu.drivevideo.video;

import android.hardware.Camera;

import com.dudu.drivevideo.utils.TakePictureTools;

/**
 * Created by dengjun on 2016/2/19.
 * Description :
 */
public class PictureObtain implements Camera.PictureCallback {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        TakePictureTools.savePictureData(data, TakePictureTools.getOutputMediaFile(TakePictureTools.MEDIA_TYPE_IMAGE));
    }
}
