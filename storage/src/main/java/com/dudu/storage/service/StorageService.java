package com.dudu.storage.service;

import com.dudu.storage.core.FileStorage;
import com.dudu.storage.core.IReadCallBack;
import com.dudu.storage.core.IStorage;
import com.dudu.storage.utils.FileTools;
import com.dudu.storage.utils.MultiHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public class StorageService {

    private IStorage iStorage;

    private String fileName = "message.txt";
    private String filePath = "/dudu/message";
    private String storagePath;
    //缓存数据
    private List<String> dataStringMemCache;

    private ScheduledExecutorService storageThreadPool = null;

    private Logger log;

    private boolean isRunningSaveThread = false;

    private String storageOrReadLock = "storageOrReadLock";

    private IReadCallBack iReadCallBack = null;

    public StorageService() {
        iStorage = new FileStorage();
        dataStringMemCache = Collections.synchronizedList(new ArrayList<String>());
        storageThreadPool = Executors.newScheduledThreadPool(1);

        log = LoggerFactory.getLogger("storage");

        storagePath = FileTools.getStoragePath() + "/" + fileName;
    }

    //存储线程
    Thread saveThread = new Thread() {
        @Override
        public void run() {
            synchronized (storageOrReadLock) {
//                setIsRunningSaveThread(true);
                try {
                    log.info("运行存储线程");
//                log.debug("路径：{}", FileTools.getStoragePath());
                    log.debug("路径：{}", storagePath);
                    log.debug("MemCache 大小：{}", dataStringMemCache.size());
                    if (dataStringMemCache.size() == 0)
                        return;
                    iStorage.saveData(dataStringMemCache, storagePath);
                    dataStringMemCache.clear();

                    sleep(1 * 1000);//延时2秒
                    log.debug("运行存储线程--完成");
                } catch (Exception e) {
                    log.error("异常{}", e);
                    e.printStackTrace();
                }
//                setIsRunningSaveThread(true);
            }
        }
    };

    //读取线程
    Thread readThread = new Thread() {
        @Override
        public void run() {
            synchronized (storageOrReadLock) {
                try {
                    log.info("运行读取线程");
                    List<String> dataStringList = iStorage.readData(storagePath);
                    if (dataStringList == null)
                        return;
                    /*for(int i = 0; i < dataStringList.size(); i++){
                        log.debug("读取字符串：{}", dataStringList.get(i));
                    }*/
                    if ((iReadCallBack != null)) {
                        iReadCallBack.onReadData(dataStringList);
                    }

                    FileTools.deleteFile(storagePath);
                    log.info("运行读取线程--结束");
                } catch (Exception e) {
                    log.error("异常{}", e);
                    e.printStackTrace();
                }
            }
        }
    };

    public void saveData(String dataString) {
        dataStringMemCache.add(dataString);
//        log.debug("dataStringMemCache 大小：{}"，dataStringMemCache.size());
    }

    public void readData(IReadCallBack iReadCallBack) {
        this.iReadCallBack = iReadCallBack;
        storageThreadPool.execute(readThread);
    }

    //立马执行保存线程
    public void flush() {
        if (dataStringMemCache.size() > 0)
            if (!isRunningSaveThread())//没在跑的时候，才开启
                storageThreadPool.execute(saveThread);
    }

    public void release() {
        if (storageThreadPool != null && !storageThreadPool.isShutdown()) {
            try {
                storageThreadPool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("异常{}", e);
                e.printStackTrace();
            }
        }
    }

    private synchronized boolean isRunningSaveThread() {
        return isRunningSaveThread;
    }

    private synchronized void setIsRunningSaveThread(boolean isRunningSaveThread) {
        this.isRunningSaveThread = isRunningSaveThread;
    }
}
