package com.dudu.workflow.robbery;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.obd.OBDStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/19.
 */
public class RobberyFlow {

    private Logger logger = LoggerFactory.getLogger("RobberyFlow");

    private static RobberyFlow mInstance = new RobberyFlow();

    public static RobberyFlow getInstance() {
        return mInstance;
    }

    public Observable<ReceiverData> syncAppRobberyFlow() {
        return ObservableFactory.getReceiverObservable()
                .filter(data -> data.getTitle().equals(ReceiverData.ROBBERY_VALUE));
    }

    public Observable<Boolean> gun3Toggle() throws IOException {
        return OBDStream.getInstance().engSpeedStream()
                .map(aDouble -> aDouble > 3000)
                .distinctUntilChanged()
                .filter(aBoolean -> aBoolean)
                .take(3)
                .timeout(60, TimeUnit.SECONDS);
    }
}
