package com.dudu.storage;

import com.dudu.storage.core.IReadCallBack;
import com.dudu.storage.service.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public class Storage {
    private static Storage instance = null;

    private StorageService storageService;

    public static  Storage getInstance(){
        if (instance == null){
            synchronized (Storage.class){
                if (instance == null){
                    instance = new Storage();
                }
            }
        }
        return instance;
    }

    public Storage() {
        storageService = new StorageService();
    }

    public void init(){
//        storageService.saveData("monitor");
////        storageService.flush();
//        storageService.readData(null);
    }

    public void release(){
        storageService.flush();
        storageService.release();
    }

    //触发保存线程运行
    public void flush(){
        storageService.flush();
    }

    public void saveData(String dataString){
        storageService.saveData(dataString);
    }

    public void readData(IReadCallBack iReadCallBack){
        storageService.readData(iReadCallBack);
    }
}
