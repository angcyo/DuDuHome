package com.dudu.drivevideo;

import android.widget.ImageView;

import com.dudu.drivevideo.video.FrontCameraDriveVideo;
import com.dudu.drivevideo.video.RearCameraDriveVideo;
import com.dudu.drivevideo.service.FrontDriveVideoService;
import com.dudu.drivevideo.service.RearCameraVideoService;

/**
 * Created by dengjun on 2016/1/26.
 * Description :
 */
public class DriveVideo {
    private static DriveVideo  instance = null;

    private RearCameraVideoService rearCameraVideoService;

    private FrontDriveVideoService frontDriveVideoService;

    public static DriveVideo getInstance(){
        if (instance == null){
            synchronized (DriveVideo.class){
                if (instance == null){
                    instance = new DriveVideo();
                }
            }
        }
        return instance;
    }

    private DriveVideo() {
        rearCameraVideoService = new RearCameraVideoService();
        frontDriveVideoService = new FrontDriveVideoService();
    }

    public void startDriveVideo(){
        rearCameraVideoService.startDriveVideo();
        frontDriveVideoService.startDriveVideo();
    }

    public void stopDriveVideo(){
        rearCameraVideoService.stopDriveVideo();
        frontDriveVideoService.stopDriveVideo();
    }

    public void setImageView(ImageView imageView){
        rearCameraVideoService.getRearCameraDriveVideo().setImageView(imageView);
    }


    public RearCameraDriveVideo getRearCameraDriveVideo(){
        return rearCameraVideoService.getRearCameraDriveVideo();
    }

    public RearCameraVideoService getRearCameraVideoService() {
        return rearCameraVideoService;
    }

    public FrontCameraDriveVideo getFrontCameraDriveVideo(){
        return frontDriveVideoService.getFrontCameraDriveVideo();
    }
}
