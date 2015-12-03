package com.dudu.conn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.network.event.DataExceptionAlarm;
import com.dudu.network.event.DataOverstepAlarm;
import com.dudu.network.event.FlowSynConfigurationRes;
import com.dudu.network.event.FlowUploadResponse;
import com.dudu.network.event.GetFlowResponse;
import com.dudu.network.event.SwitchFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by xuzhao on 2015/12/2.
 */
public class FlowManage {
    private static FlowManage instance = null;
    private Context mContext;
    private Logger log;
    private List<FlowManage> flowDataList;

    public static FlowManage getInstance(Context context){
        if (instance == null){
            synchronized (FlowManage.class){
                if (instance == null){
                    instance = new FlowManage(context);
                }
            }
        }
        return instance;
    }
    public FlowManage(Context context) {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        log = LoggerFactory.getLogger("FlowManage");
        mContext=context;
        flowDataList = new ArrayList<>();
    }

    /**
     * 流量查询处理
     *
     * @return
     */
    public void onEventBackgroundThread(GetFlowResponse getFlowResponse) {
        float remianFlow= getFlowResponse.getRemainingFlow();
        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }

    /**
     * 流量上报处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowUploadResponse flowUploadResponse) {
        float remianFlow= flowUploadResponse.getRemainingFlow();
        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }

    /**
     * 流量策略配置同步处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowSynConfigurationRes flowSynConfigurationRes) {
        String portalVersion = flowSynConfigurationRes.getPortalVersion();
        String portalAddress = flowSynConfigurationRes.getPotalAddress();
        if (!TextUtils.isEmpty(portalVersion)) {
            PortalUpdate.getInstance(mContext).updatePortal(mContext, portalVersion, portalAddress);
        }

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_TRAFFICE_CONTROL,
                String.valueOf(flowSynConfigurationRes.getTrafficControl()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_MONTH_MAX_VALUE,
                String.valueOf(flowSynConfigurationRes.getMonthMaxValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ADD_VALUE,
                String.valueOf(flowSynConfigurationRes.getFreeAddValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DAILY_MAX_VALUE,
                String.valueOf(flowSynConfigurationRes.getDailyMaxValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_UP_LIMIT_MAX_VALUE,
                String.valueOf(flowSynConfigurationRes.getUpLimitMaxValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_PORTAL_ADDRESS,
                String.valueOf(flowSynConfigurationRes.getPotalAddress()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_PORTAL_VERSION,
                String.valueOf(flowSynConfigurationRes.getPortalVersion()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DOWN_LIMIT_MAX_VALUE,
                String.valueOf(flowSynConfigurationRes.getDownLimitMaxValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_LIFE_TYPE,
                String.valueOf(flowSynConfigurationRes.getLifeType()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_UPLOAD_LIMIT,
                String.valueOf(flowSynConfigurationRes.getUploadLimit()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ADD_TIMES,
                String.valueOf(flowSynConfigurationRes.getFreeAddTimes()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_MIDDLE_ARLAM_VALUE,
                String.valueOf(flowSynConfigurationRes.getMiddleArlamValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_HIGH_ARLAM_VALUE,
                String.valueOf(flowSynConfigurationRes.getHighArlamValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_LOW_ARLAM_VALUE,
                String.valueOf(flowSynConfigurationRes.getLowArlamValue()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DOWNLOAD_LIMIT,
                String.valueOf(flowSynConfigurationRes.getDownloadLimit()));

        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ARRIVE_VALUE,
                String.valueOf(flowSynConfigurationRes.getFreeArriveValue()));

    }

    /**
     * 流量开关
     *
     * @return
     */
    public void onEventBackgroundThread(SwitchFlow switchFlow) {
        switch (switchFlow.getTrafficControl()) {
            case ConnectionConstants.RESULT_TRAFFIC_CONTROL_CLOSE:
                WifiApAdmin.closeWifiAp(mContext);
                break;

            case ConnectionConstants.RESULT_TRAFFIC_CONTROL_OPEN:
                WifiApAdmin.initWifiApState(mContext);
                break;
        }
    }

    /**
     * 流量超限预警
     *
     * @return
     */
    public void onEventBackgroundThread(DataOverstepAlarm dataOverstepAlarm) {
        switch (dataOverstepAlarm.getAlarmLevel()) {
            case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                //   0:正常
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_ADVANCED_WARNING:
                //   1:高级预警   当前剩余值<最大阀值10%
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_INTERMEDIATE_WARNING:
                //   2:中级预警    当前剩余值<最大阀值15%
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_LOWLEVEL_WARNING:
                //   3:低级预警    当前剩余值<最大阀值20%
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_CLOSE:
                //   4:关闭        当前剩余值<最大阀值5%
                break;
        }
    }

    /**
     * 流量异常预警
     *
     * @return
     */
    public void onEventBackgroundThread(DataExceptionAlarm dataExceptionAlarm) {
        switch (dataExceptionAlarm.getAlarmLevel()) {
            case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                //   0:正常
                break;
            case 1:
                //   1:关闭（后台说的是关闭）
                break;
        }
    }
    public List<FlowManage> getObdDataList() {
        return flowDataList;
    }
    /* 释放资源*/
    public void release(){
        EventBus.getDefault().unregister(this);
        instance = null;
    }
}
