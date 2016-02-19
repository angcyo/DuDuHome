package com.dudu.workflow.driving;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.rest.model.AccTestData;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.RequestFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DrivingFlow {

    private Logger logger = LoggerFactory.getLogger("DrivingFlow");

    public void getReceiveDataFlow() {
        ObservableFactory.getReceiverObservable()
                .filter(new Func1<ReceiverData,Boolean>() {
                    @Override
                    public Boolean call(ReceiverData data) {
                        return data.getTitle().equals(ReceiverData.ACCELERATEDTESTSTART_VALUE);
                    }
                })
                .map(new Func1<ReceiverData, AccTestData>() {
                    @Override
                    public AccTestData call(ReceiverData receiverData) {
                        String type = receiverData.getContent();
                        return getAccTestData(getRamdomData(type), type);
                    }
                })
                .subscribe(new Action1<AccTestData>() {
                    @Override
                    public void call(AccTestData data) {
                        RequestFactory.getDrivingRequest()
                                .pushAcceleratedTestData(data, new DrivingRequest.RequesetCallback() {
                                    @Override
                                    public void requestSuccess(boolean success) {
                                        if (success) {
                                            logger.debug("发送加速数据到服务端成功");
                                        } else {
                                            logger.debug("发送加速数据到服务端成功");

                                        }
                                    }
                                });
                    }
                });
    }

    public AccTestData getAccTestData(String value, String type) {
        AccTestData accTestData = new AccTestData();
        accTestData.setAccTotalTime(value);
        accTestData.setAccType(type);
        accTestData.setDateTime(System.currentTimeMillis()+"");
        return accTestData;
    }

    public String getRamdomData(String type) {
        switch (type) {
            case "1":
                return String.valueOf(Math.random() * 10);
            case "2":
                return String.valueOf(Math.random() * 10);
            case "3":
                return String.valueOf(Math.random() * 10);
        }
        return "0";
    }
}
