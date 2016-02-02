package com.dudu.conn;

import android.content.Context;
import android.text.TextUtils;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.network.event.DataExceptionAlarm;
import com.dudu.network.event.DataOverstepAlarm;
import com.dudu.network.event.FlowSynConfigurationRes;
import com.dudu.network.event.FlowUploadResponse;
import com.dudu.network.event.GetFlowResponse;
import com.dudu.network.event.SwitchFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import de.greenrobot.event.EventBus;


/**
 * Created by xuzhao on 2015/12/2.
 */
public class FlowManage {
    private static FlowManage instance = null;
    private Context mContext;
    private Logger log;


    public static FlowManage getInstance(Context context) {
        if (instance == null) {
            synchronized (FlowManage.class) {
                if (instance == null) {
                    instance = new FlowManage(context);
                }
            }
        }
        return instance;
    }

    public FlowManage(Context context) {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        log = LoggerFactory.getLogger("monitor");
        mContext = context;
    }

    /**
     * 流量查询处理
     *
     * @return
     */
    public void onEventBackgroundThread(GetFlowResponse getFlowResponse) {
        float remianFlow = getFlowResponse.getRemainingFlow();
        log.info("GetFlowResponse剩余流量：{}", remianFlow);
        SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }

    /**
     * 流量上报处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowUploadResponse flowUploadResponse) {
        float remianFlow = flowUploadResponse.getRemainingFlow();
        log.info("FlowUploadResponse剩余流量：{}",remianFlow);
        SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));

        log.info("流量开关：{}", flowUploadResponse.getTrafficControl());
        proSwitchFlow(flowUploadResponse.getTrafficControl());

        log.info("当日流量超限异常状态：{}", flowUploadResponse.getExceptionState());
        log.info("每月流量告警状态：{}", flowUploadResponse.getTrafficState());
    }

    /**
     * 流量策略配置同步处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowSynConfigurationRes flowSynConfigurationRes) {
        try {
            String portalVersion = flowSynConfigurationRes.getPortalVersion();
            String portalAddress = flowSynConfigurationRes.getPortalAddress();
            if (!TextUtils.isEmpty(portalVersion)) {
                PortalUpdate.getInstance(mContext).updatePortal(mContext, portalVersion, portalAddress);
            }
        } catch (Exception e) {
            log.error("异常：{}", e);
        }
        try {
//            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_TRAFFICE_CONTROL,
//                    flowSynConfigurationRes.getTrafficControl());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_MONTH_MAX_VALUE,
                    flowSynConfigurationRes.getMonthMaxValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_FREE_ADD_VALUE,
                    flowSynConfigurationRes.getFreeAddValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_DAILY_MAX_VALUE,
                    flowSynConfigurationRes.getDailyMaxValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_UP_LIMIT_MAX_VALUE,
                    flowSynConfigurationRes.getUpLimitMaxValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_PORTAL_ADDRESS,
                    flowSynConfigurationRes.getPortalAddress());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_PORTAL_VERSION,
                    flowSynConfigurationRes.getPortalVersion());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_DOWN_LIMIT_MAX_VALUE,
                    flowSynConfigurationRes.getDownLimitMaxValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_LIFE_TYPE,
                    flowSynConfigurationRes.getLifeType());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_UPLOAD_LIMIT,
                    flowSynConfigurationRes.getUploadLimit());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_FREE_ADD_TIMES,
                    flowSynConfigurationRes.getFreeAddTimes());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_MIDDLE_ARLAM_VALUE,
                    flowSynConfigurationRes.getMiddleArlamValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_HIGH_ARLAM_VALUE,
                    flowSynConfigurationRes.getHighArlamValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_LOW_ARLAM_VALUE,
                    flowSynConfigurationRes.getLowArlamValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_DOWNLOAD_LIMIT,
                    flowSynConfigurationRes.getDownloadLimit());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_FREE_ARRIVE_VALUE,
                    flowSynConfigurationRes.getFreeArriveValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_CLOSER_ARLAM_VALUE,
                    flowSynConfigurationRes.getCloseArlamValue());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_FLOW_FREQUENCY,
                    flowSynConfigurationRes.getFlowFrequency());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_GPS_FREQUENCU,
                    flowSynConfigurationRes.getGpsFrequency());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_PORTAL_COUNT_FREQUENCY,
                    flowSynConfigurationRes.getPortalCountFrequency());

//            log.info("FlowUploadResponse剩余流量：{}", flowSynConfigurationRes.getRemainingFlow());
//            SharedPreferencesUtils.putStringValue(mContext,Constants.KEY_REMAINING_FLOW, flowSynConfigurationRes.getRemainingFlow());

            SharedPreferencesUtils.putStringValue(mContext, Constants.KEY_UPLOAD_FLOW_VALUE, flowSynConfigurationRes.getUploadFlowValue());

        } catch (Exception e) {
            log.error("异常：{}", e);
        }
    }

    /**
     * 流量开关
     *
     * @return
     */
    public void onEventBackgroundThread(SwitchFlow switchFlow) {
        log.info("流量开关：{}",switchFlow.getTrafficControl());
        proSwitchFlow(switchFlow.getTrafficControl());
    }

    private void proSwitchFlow(int switchState){
        switch (switchState) {
            case ConnectionConstants.RESULT_TRAFFIC_CONTROL_OPEN:
                WifiApAdmin.initWifiApState(mContext);
                break;
            case ConnectionConstants.RESULT_TRAFFIC_CONTROL_CLOSE:
                WifiApAdmin.closeWifiAp(mContext);
                break;
        }
    }

    /**
     * 流量超限预警
     *
     * @return
     */
    public void onEventBackgroundThread(DataOverstepAlarm dataOverstepAlarm) {
        log.info("流量超限报警值：{}", dataOverstepAlarm.getAlarmLevel());
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
//                WifiApAdmin.closeWifiAp(mContext);
                break;
        }
    }

    /**
     * 流量异常预警
     *
     * @return
     */
    public void onEventBackgroundThread(DataExceptionAlarm dataExceptionAlarm) {
        log.info("流量异常报警值：{}",dataExceptionAlarm.getAlarmLevel());
        switch (dataExceptionAlarm.getAlarmLevel()) {
            case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                //   0:正常
//                WifiApAdmin.closeWifiAp(mContext);
                break;
            case 1:
                //   1:关闭（后台说的是关闭）

                break;
        }
    }



    /* 释放资源*/
    public void release() {
        EventBus.getDefault().unregister(this);
        instance = null;
    }
    public void onEventMainThread(Port port){
//        Toast.makeText(mContext, port.data, Toast.LENGTH_SHORT).show();//提示先去掉
    }

    public class Port{
        String data;
        public Port(String data){
            this.data = data;
        }
    }
}
