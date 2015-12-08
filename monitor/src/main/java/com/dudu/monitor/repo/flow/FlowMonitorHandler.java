package com.dudu.monitor.repo.flow;

import android.content.Context;

import com.dudu.monitor.utils.SharedPreferencesUtil;
import com.dudu.network.event.FlowUploadResponse;
import com.dudu.network.event.GetFlowResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/12/8.
 * Description : 流量监控处理器，用于处理发送和接收消息
 */
public class FlowMonitorHandler {
    private Logger log;
    private Context mContext;
    public FlowMonitorHandler() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        log = LoggerFactory.getLogger("monitor");
    }

    public void init(Context context){
        this.mContext = context;
    }

    public void release(){
        EventBus.getDefault().unregister(this);
    }


    /**
     * 流量查询处理
     *
     * @return
     */
    public void onEventBackgroundThread(GetFlowResponse getFlowResponse) {
        float remianFlow = getFlowResponse.getRemainingFlow();
        log.info("GetFlowResponse剩余流量：{}",remianFlow);
        SharedPreferencesUtil.putStringValue(mContext, FlowConfigConstant.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }

    /**
     * 流量上报处理
     *
     * @return
     */
    public void onEventBackgroundThread(FlowUploadResponse flowUploadResponse) {
        float remianFlow = flowUploadResponse.getRemainingFlow();
        log.info("FlowUploadResponse剩余流量：{}",remianFlow);
        SharedPreferencesUtil.putStringValue(mContext, FlowConfigConstant.KEY_REMAINING_FLOW, String.valueOf(remianFlow));
    }


}
