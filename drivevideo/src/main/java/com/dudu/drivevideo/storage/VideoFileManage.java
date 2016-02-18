package com.dudu.drivevideo.storage;

import com.dudu.drivevideo.model.VideoEntity;
import com.dudu.drivevideo.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by dengjun on 2016/2/18.
 * Description :
 */
public class VideoFileManage {
    private static VideoFileManage instance = null;

    private DbHelper dbHelper;

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
        File videoFile = new File(videoFileAbPath);
        float sise = Float.parseFloat(FileUtil.fileByte2Mb(videoFile.length()));
        if (sise < 250){
            log.info("250Kb以下的文件不保存");
            videoFile.delete();
        }

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
}
