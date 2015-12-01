package com.dudu.conn;

import android.content.Context;
import android.util.Log;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.fdfs.common.MyException;
import com.dudu.fdfs.fastdfs.FileProcessUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxh on 2015/11/7.
 */
public class PortalUpdate {

    public static final String FDFS_CLIEND_NAME = "fdfs_client.conf";
    public static final String NODOGSPLASH_NAME = "nodogsplash";
    public static final String TEMP_ZIP_FOLDER_NAME = "temp_zip";
    public static final String HTDOCS_FOLDER_NAME = "/htdocs";
    public static final String HTDOCS_ZIP_NAME = "htdocs.zip";
    public static final String TEMP_ZIP_NAME = "temp.zip";
    private static final String TAG = "PortalUpdate";

    private static PortalUpdate mInstance;

    public static PortalUpdate getInstance() {
        if (mInstance == null) {
            mInstance = new PortalUpdate();
        }

        return mInstance;
    }

    private PortalUpdate() {

    }

    /**
     * @param context 上下文
     * @param method  方法名
     * @param url     下载地址
     * @param group   下载方式
     *  后台发送指令，通知Portal更新
     */
    public void handleUpdate(final Context context, String method, final String url, final String group) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = 1;
                try {
                    //嘟嘟相关文件存储的位置
                    File dirFile = new File(FileUtils.getExternalStorageDirectory(), NODOGSPLASH_NAME);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    //放置压缩包的路径
                    File zipDirFile = new File(dirFile.getPath(), TEMP_ZIP_FOLDER_NAME);
                    if (!zipDirFile.exists()) {
                        zipDirFile.mkdirs();
                    }
                    //存放解压文件的额目录
                    File desFile = new File(dirFile.getPath(), HTDOCS_FOLDER_NAME);
                    if (!desFile.exists()) {
                        desFile.mkdirs();
                    }
                    File fdfsFile = new File(dirFile.getPath(), FDFS_CLIEND_NAME);
                    if (!fdfsFile.exists()) {
                        fdfsFile.createNewFile();
                    }
                    InputStream isAsset = context.getAssets().open(FDFS_CLIEND_NAME);
                    if (FileUtils.copyFileToSd(isAsset, fdfsFile)) {
                        String path = fdfsFile.getAbsolutePath();
                        //请求网络，下载压缩文件到指定路径下
                        /***
                         * 参数一：网络请求的路径
                         * 参数二:服务器上压缩文件的名字
                         * 参数三：本地存放的路径
                         * 参数四：本地存放的重命名
                         * */
                        result = FileProcessUtil.getInstance(path).downloadFile(group, url, zipDirFile.getPath() + "/", TEMP_ZIP_NAME);
                        Log.i("ji", "" + result);
                    }
                    if (result == 0) {
                        //如果返回的结果为0的话，则下载成功
                        File zipPath = new File(zipDirFile.getPath(),TEMP_ZIP_NAME);
                        if (zipPath.exists()) {
                            //解压文件
                            FileUtils.upZipFile(zipPath,dirFile.getPath());

                            updatePortalVersion(context);
                        }
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG, e.toString());
                } catch (MyException e) {
                    LogUtils.e(TAG,e.toString());
                }
            }
        }).start();
    }

    public void updatePortal(Context context, String version, String address) {
        String localVersion = SharedPreferencesUtil.getStringValue(context, Constants.KEY_PORTAL_VERSION, "0");
        if (!version.equals(localVersion)) {
            String [] portalAddress = address.split(",");
            String groupName = portalAddress[0];
            String fileName = portalAddress[1];

            handleUpdate(context, "", fileName, groupName);
        }
    }

    private void updatePortalVersion(Context context) {
        try {
            int version = Integer.valueOf(SharedPreferencesUtil.getStringValue(context,
                    Constants.KEY_PORTAL_VERSION, "0"));
            SharedPreferencesUtil.putStringValue(context, Constants.KEY_PORTAL_VERSION,
                    String.valueOf(version + 1));
        } catch (NumberFormatException e) {
            // ignore
        }
    }

}
