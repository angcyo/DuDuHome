package com.dudu.monitor.repo.flow;

import com.dudu.monitor.valueobject.FlowInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dengjun on 2015/12/8.
 * Description :流量管理类，用于处理流量相关的后台操作
 */
public class FlowManage {
    private FlowInfo flowInfo;

    private Logger log;

    public FlowManage() {
        log = LoggerFactory.getLogger("monitor");
    }
}
