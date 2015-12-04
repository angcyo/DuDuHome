package com.dudu.storage.core;

import java.util.List;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public interface IReadCallBack {
//    public void onReadData(String key, String dataString);
//    public void onReadData(String key, List<String> dataString);

    public void onReadData(List<String> dataStringList);
}
