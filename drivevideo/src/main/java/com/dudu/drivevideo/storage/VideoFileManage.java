package com.dudu.drivevideo.storage;

import android.net.Uri;

import com.dudu.drivevideo.config.DriveVideoContants;
import com.dudu.drivevideo.model.PhotoInfoEntity;
import com.dudu.drivevideo.model.VideoEntity;
import com.dudu.drivevideo.utils.FileUtil;
import com.dudu.drivevideo.utils.ImageLoadTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dengjun on 2016/2/18.
 * Description :
 */
public class VideoFileManage {
    private static VideoFileManage instance = null;

    private DbHelper dbHelper;

    private List<PhotoInfoEntity> photoInfoEntityList;

    private Logger log;

    public static VideoFileManage getInstance(){
         if (instance == null){
             synchronized (VideoFileManage.class){
                 if (instance == null){
                     instance = new VideoFileManage();
                 }
             }
         }
        return instance;
    }

    private VideoFileManage() {
        dbHelper = DbHelper.getDbHelper();
        log = LoggerFactory.getLogger("video.frontdrivevideo");
    }

    public void saveVideoInfo(String videoFileAbPath){
        saveVideoInfoToDb(videoFileAbPath);
    }

    private void saveVideoInfoToDb(final String videoFileAbPath){
        if (videoFileAbPath == null)
            return;
        File videoFile = new File(videoFileAbPath);
        float sise = Float.parseFloat(FileUtil.fileByte2Kb(videoFile.length()));
       /* if (sise < 250){
            log.info("250Kb以下的文件不保存");
            videoFile.delete();
            return;
        }*/

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setCreateTime(videoFile.getName());
        videoEntity.setFile(videoFile);
        videoEntity.setName(videoFile.getAbsolutePath());
        videoEntity.setSize(FileUtil.fileByte2Mb(videoFile.length()));

        dbHelper.insertVideo(videoEntity);
    }

    public void guardTFCardSpace(){
        float tfCardTotalSize = Float.parseFloat(FileUtil.fileByte2Mb(FileUtil.getTFlashCardSpace()));
        if (tfCardTotalSize <= dbHelper.getTotalSize()){
            log.debug("删除时间最久的视频...");
            dbHelper.deleteOldestVideo();

            FileUtil.clearLostDirFolder();
        }
    }

    public LinkedList<VideoEntity> getVideoList(){
        return dbHelper.getAllVideos();
    }

    public DbHelper getDbHelper(){
        return dbHelper;
    }

    public List<PhotoInfoEntity> getPhotoInfoEntityList() {
        return photoInfoEntityList;
    }

    public List<PhotoInfoEntity> generatePhotoInfoEntityList(){
        File photoStorageDir = FileUtil.getTFlashCardDirFile(DriveVideoContants.REAR_VIDEO_STORAGE_PARENT_PATH,
                DriveVideoContants.FRONT_PICTURE_STORAGE_PATH);
        List<Uri> photoUriList = ImageLoadTools.getDirPhotoUriList(photoStorageDir.getAbsolutePath());
        List<PhotoInfoEntity> photoInfoEntityList = null;
        if (photoUriList != null){
            photoInfoEntityList = new ArrayList<PhotoInfoEntity>();
            for(Uri uri: photoUriList){
                PhotoInfoEntity photoInfoEntity = new PhotoInfoEntity();
                photoInfoEntity.setPhotoInfoUri(uri);
                photoInfoEntityList.add(photoInfoEntity);
            }
        }
        return photoInfoEntityList;
    }
}
