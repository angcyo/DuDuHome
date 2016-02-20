package com.dudu.workflow.driving;

import android.util.Log;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.rest.model.AccTestData;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.obd.OBDStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DrivingFlow {

    private Logger logger = LoggerFactory.getLogger("DrivingFlow");

    public void getReceiveDataFlow() {
        Observable<String> type = ObservableFactory.getReceiverObservable()
                .filter(data -> data.getTitle().equals(ReceiverData.ACCELERATEDTESTSTART_VALUE))
                .map(ReceiverData::getContent)
                .doOnNext(s -> {
                    try {
                        OBDStream.getInstance().exec("ATTSPMON");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                });

        Observable<String> speed_time = type
                .flatMap(max_speed -> {
                    try {
                        return OBDStream.getInstance().testSpeedStream()
                                .takeUntil(aDouble -> aDouble > Integer.parseInt(max_speed) * 100)
                                .count()
                                .map(integer -> integer * 0.2)
                                .doOnNext(aDouble -> {
                                    try {
                                        OBDStream.getInstance().exec("ATTSPMOFF");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                })
                .map(Object::toString);

        Observable.combineLatest(type, speed_time, (type1, speed_time1) -> new AccTestData(type1, speed_time1, String.valueOf(System.currentTimeMillis())))
                .doOnNext(accTestData -> Log.d("test speed result", accTestData.toString()))
                .subscribe(data -> {
                    RequestFactory.getDrivingRequest()
                            .pushAcceleratedTestData(data, success -> {
                                if (success) {
                                    logger.debug("发送加速数据到服务端成功");
                                } else {
                                    logger.debug("发送加速数据到服务端成功");

                                }
                            });
                });
    }

}
