package com.dudu.storage.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public class FileStorage implements IStorage{
    private Logger log = LoggerFactory.getLogger("storage");


    @Override
    public void saveData(List<String> dataStringList, String filePath) {
        File fileToSave = new File(filePath);
        if (!fileToSave.exists()){
            try {
                fileToSave.createNewFile();
            } catch (IOException e) {
                log.error("异常{}",e);
                e.printStackTrace();
            }
        }
        FileWriter fileWriter = null;
        try {
//            log.debug("open- fileWriter");
            fileWriter = new FileWriter(filePath, true);
            for (int i = 0; i< dataStringList.size(); i++){
//                log.debug("open- fileWriter-for：{}", i);
                fileWriter.write(dataStringList.get(i));
                fileWriter.write("\r\n");
            }
            fileWriter.flush();
        } catch (IOException e) {
            log.error("异常{}",e);
        }finally {
//            log.debug("run finally------close--- fileWriter--");
            if (fileWriter != null){
                try {
                    log.debug("close--- fileWriter");
                    fileWriter.close();
                } catch (IOException e) {
                    log.error("异常{}",e);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> readData(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        List<String> dataStringList = new ArrayList<String >();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String dataString = null;
            while ((dataString = bufferedReader.readLine()) != null){
                dataStringList.add(dataString);
            }
        } catch (FileNotFoundException e) {
            log.error("异常{}",e);
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fileReader != null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    log.error("异常{}",e);
                    e.printStackTrace();
                }
            }
        }
        return dataStringList;
    }
}
