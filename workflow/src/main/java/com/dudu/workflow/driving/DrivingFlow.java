package com.dudu.workflow.driving;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.commonlib.utils.RxBus;
import com.dudu.workflow.RequestFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DrivingFlow {

    private Logger logger = LoggerFactory.getLogger("DrivingFlow");

    public void getReceiveDataFlow(){
        RxBus.getInstance().asObservable()
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object event) {
                        return event instanceof ReceiverData;
                    }
                })
                .map(new Func1<Object, ReceiverData>() {
                    @Override
                    public ReceiverData call(Object event) {
                        return (ReceiverData)event;
                    }
                })
                .subscribe(new Action1<ReceiverData>() {
                    @Override
                    public void call(ReceiverData data) {
                        RequestFactory.getDrivingRequest()
                                .pushAcceleratedTestData(getRamdomData(data.getSwitchContent()), new DrivingRequest.RequesetCallback() {
                                    @Override
                                    public void requestSuccess(boolean success) {
                                        if(success) {
                                            logger.debug("发送加速数据到服务端成功");
                                        }else{
                                            logger.debug("发送加速数据到服务端成功");

                                        }
                                    }
                                });
                    }
                });
    }

    public double getRamdomData(int type){
        switch (type){
            case 1:
                return Math.random() * 10;
            case 2:
                return Math.random() * 10;
            case 3:
                return Math.random() * 10;
        }
        return 0;
    }
}
