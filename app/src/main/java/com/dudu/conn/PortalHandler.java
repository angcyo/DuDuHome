package com.dudu.conn;

import android.content.Context;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.fdfs.common.MyException;
import com.dudu.fdfs.fastdfs.FileProcessUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * Created by lxh on 2015/11/7.
 */
public class PortalHandler {
private static final String ZIPFILE_NAME="zipFile.zip";
    private static final String FDFS_CLIEND_NAME="fdfs_client.conf";
    private static final String NODOGSPLASH_NAME="nodogsplash";
    public PortalHandler(){

    }

    /**
     *@param context 上下文
     * @param method 方法名
     * @param url   下载地址
     * @param group 下载方式
     */
    public void handlerUpdate(final Context context,String method, final String url, final String group){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result=0;
                try {
                    //把Assets下的fdfs_client.conf(服务器相关的配置文件)的文件复制到sd卡下
                    File dirFile=new File(FileUtils.getExternalStorageDirectory(),NODOGSPLASH_NAME);
                    if(!dirFile.exists()){
                        dirFile.mkdirs();
                    }
                    File fdfsFile=new File(dirFile.getPath(),FDFS_CLIEND_NAME);
                    if(!fdfsFile.exists()){
                        fdfsFile.createNewFile();
                    }
                    InputStream isAsset=context.getAssets().open(FDFS_CLIEND_NAME);
                    if(FileUtils.copyFileToSd(isAsset,fdfsFile)){
                        String path=fdfsFile.getAbsolutePath();
                        //请求网络，下载压缩文件到指定路径下
                        /***
                         * 参数一：网络请求的路径
                         * 参数二:服务器上压缩文件的名字
                         * 参数三：本地存放的路径
                         * 参数四：本地存放的重命名
                         * */
                        result = FileProcessUtil.getInstance(path).downloadFile(group,url,dirFile.getPath() + "/",ZIPFILE_NAME);
                    }
                    //如果返回的结果为0的话，则下载成功
                   if (result==0){
                        File file1=new File(dirFile.getPath(),ZIPFILE_NAME);
                        if(file1.exists()){
                            //获得压缩文件
                            ZipFile zipFile=new ZipFile(file1);
                            //解压文件
                            FileUtils.upZipFile(zipFile);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
