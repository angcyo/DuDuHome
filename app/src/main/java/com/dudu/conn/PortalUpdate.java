package com.dudu.conn;

import android.content.Context;
import android.util.Log;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.SharedPreferencesUtils;
import com.dudu.fdfs.common.MyException;
import com.dudu.fdfs.fastdfs.FileProcessUtil;
import com.dudu.network.event.UpdatePortal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

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
    private Context mContext;
    private Logger log;
    private static PortalUpdate instance = null;

    public static PortalUpdate getInstance(Context context) {
        if (instance == null) {
            synchronized (PortalUpdate.class) {
                if (instance == null) {
                    instance = new PortalUpdate(context);
                }
            }
        }
        return instance;
    }

    public PortalUpdate(Context context) {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        log = LoggerFactory.getLogger("monitor");
        mContext = context;
    }

    /**
     * PortalUpdate更新处理
     *
     * @return
     */
    public void onEventBackgroundThread(UpdatePortal updateportal) {
        Log.v("FlowManage", "开始");
        log.info("接收到更新portal事件");
        String group = updateportal.getGroup_name();
        String url = updateportal.getUrl();
        refreshPortal(group, url);
        Log.v("FlowManage", "portalUpdateRes");
    }

    public void updatePortal(Context context, String version, String address) {
        String localVersion = SharedPreferencesUtils.getStringValue(context, Constants.KEY_PORTAL_VERSION, "0");
        if (!version.equals(localVersion)) {
            String[] portalAddress = address.split(",");
            String groupName = portalAddress[0];
            String fileName = portalAddress[1];
            refreshPortal(fileName, groupName);
        }
    }

    private void updatePortalVersion(Context context) {
        try {
            int version = Integer.valueOf(SharedPreferencesUtils.getStringValue(context,
                    Constants.KEY_PORTAL_VERSION, "0"));
            SharedPreferencesUtils.putStringValue(context, Constants.KEY_PORTAL_VERSION,
                    String.valueOf(version + 1));
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private void refreshPortal(final String group, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = 1;
                try {
                    log.info("处理更新portal事件");
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
                    InputStream isAsset = mContext.getAssets().open(FDFS_CLIEND_NAME);
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
                        log.info("下载portal文件结果：{}",result);
                    }
                    if (result == 0) {
                        //如果返回的结果为0的话，则下载成功
                        File zipPath = new File(zipDirFile.getPath(), TEMP_ZIP_NAME);
                        if (zipPath.exists()) {
                            //解压文件
                            FileUtils.upZipFile(zipPath, dirFile.getPath());

                            updatePortalVersion(mContext);
                        }
                    }
                } catch (IOException e) {
                    log.error("异常 {}",e);
                    LogUtils.e(TAG, e.toString());
                } catch (MyException e) {
                    log.error("异常 {}",e);
                    LogUtils.e(TAG, e.toString());
                }catch (Exception e){
                    log.error("异常 {}",e);
                }
            }
        }).start();
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        instance = null;
    }
}
