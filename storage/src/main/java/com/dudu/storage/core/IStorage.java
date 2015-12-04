package com.dudu.storage.core;

import java.util.List;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public interface IStorage {
    public void saveData(List<String> dataStringList, String filePath);

    public List<String> readData(String filePath);
}
