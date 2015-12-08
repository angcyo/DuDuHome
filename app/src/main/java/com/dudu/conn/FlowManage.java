package com.dudu.conn;

import android.content.Context;
import android.text.TextUtils;

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

        log = LoggerFactory.getLogger("FlowManage");
        mContext = context;
    }

    /**
     * 流量查询处理
     *
     * @return
     */
    public void onEventBackgroundThread(GetFlowResponse getFlowResponse) {
        float remianFlow = getFlowResponse.getRemainingFlow();
        log.info("GetFlowResponse剩余流量：{}",remianFlow);
        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }

    /**
     * 流量上报处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowUploadResponse flowUploadResponse) {
        float remianFlow = flowUploadResponse.getRemainingFlow();
        log.info("FlowUploadResponse剩余流量：{}",remianFlow);
        SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
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
            e.printStackTrace();
        }
        try {
            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_TRAFFICE_CONTROL,
                    flowSynConfigurationRes.getTrafficControl());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_MONTH_MAX_VALUE,
                    flowSynConfigurationRes.getMonthMaxValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ADD_VALUE,
                    flowSynConfigurationRes.getFreeAddValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DAILY_MAX_VALUE,
                    flowSynConfigurationRes.getDailyMaxValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_UP_LIMIT_MAX_VALUE,
                    flowSynConfigurationRes.getUpLimitMaxValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_PORTAL_ADDRESS,
                    flowSynConfigurationRes.getPortalAddress());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_PORTAL_VERSION,
                    flowSynConfigurationRes.getPortalVersion());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DOWN_LIMIT_MAX_VALUE,
                    flowSynConfigurationRes.getDownLimitMaxValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_LIFE_TYPE,
                    flowSynConfigurationRes.getLifeType());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_UPLOAD_LIMIT,
                    flowSynConfigurationRes.getUploadLimit());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ADD_TIMES,
                    flowSynConfigurationRes.getFreeAddTimes());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_MIDDLE_ARLAM_VALUE,
                    flowSynConfigurationRes.getMiddleArlamValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_HIGH_ARLAM_VALUE,
                    flowSynConfigurationRes.getHighArlamValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_LOW_ARLAM_VALUE,
                    flowSynConfigurationRes.getLowArlamValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_DOWNLOAD_LIMIT,
                    flowSynConfigurationRes.getDownloadLimit());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FREE_ARRIVE_VALUE,
                    flowSynConfigurationRes.getFreeArriveValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_CLOSER_ARLAM_VALUE,
                    flowSynConfigurationRes.getCloseArlamValue());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_FLOW_FREQUENCY,
                    flowSynConfigurationRes.getFlowFrequency());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_GPS_FREQUENCU,
                    flowSynConfigurationRes.getGpsFrequency());

            SharedPreferencesUtil.putStringValue(mContext, Constants.KEY_PORTAL_COUNT_FREQUENCY,
                    flowSynConfigurationRes.getPortalCountFrequency());

//            log.info("FlowUploadResponse剩余流量：{}", flowSynConfigurationRes.getRemainingFlow());
//            SharedPreferencesUtil.putStringValue(mContext,Constants.KEY_REMAINING_FLOW, flowSynConfigurationRes.getRemainingFlow());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 流量开关
     *
     * @return
     */
    public void onEventBackgroundThread(SwitchFlow switchFlow) {
        log.info("流量开关：{}",switchFlow.getTrafficControl());
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
        log.info("流量超限报警值：{}", dataOverstepAlarm.getAlarmLevel());
        switch (dataOverstepAlarm.getAlarmLevel()) {
            case ConnectionConstants.FIELD_ALARM_LEVEL_OPEN:
                //   0:正常
//                EventBus.getDefault().post(new Port(mContext.getString(R.string.use_flow_normal)));
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_ADVANCED_WARNING:
                //   1:高级预警   当前剩余值<最大阀值10%
//                EventBus.getDefault().post(new Port(mContext.getString(R.string.use_flow_low_alarm)));
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_INTERMEDIATE_WARNING:
                //   2:中级预警    当前剩余值<最大阀值15%
//                EventBus.getDefault().post(new Port(mContext.getString(R.string.use_flow_middle_alarm)));
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_LOWLEVEL_WARNING:
                //   3:低级预警    当前剩余值<最大阀值20%
//                EventBus.getDefault().post(new Port(mContext.getString(R.string.use_flow_high_alarm)));
                break;
            case ConnectionConstants.FIELD_ALARM_LEVEL_CLOSE:
                //   4:关闭        当前剩余值<最大阀值5%
//                EventBus.getDefault().post(new Port(mContext.getString(R.string.use_close_flow)));
                WifiApAdmin.closeWifiAp(mContext);
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
                WifiApAdmin.initWifiApState(mContext);
                break;
            case 1:
                //   1:关闭（后台说的是关闭）
                WifiApAdmin.closeWifiAp(mContext);
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
