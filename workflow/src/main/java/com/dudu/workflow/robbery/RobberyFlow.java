package com.dudu.workflow.robbery;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.obd.OBDStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class RobberyFlow {

    private Logger logger = LoggerFactory.getLogger("RobberyFlow");

    private static RobberyFlow mInstance = new RobberyFlow();

    public static RobberyFlow getInstance() {
        return mInstance;
    }

    public Observable<Boolean> gun3Toggle() throws IOException {
        return OBDStream.getInstance().engSpeedStream()
                .map(aDouble -> aDouble > 1000)
                .distinctUntilChanged()
                .filter(aBoolean -> aBoolean)
                .take(3)
                .timeout(60, TimeUnit.SECONDS);
    }

    public Observable<ReceiverData> syncAppRobberyFlow(Observable<ReceiverData> observable) {
        return observable.filter(data -> data.getTitle().equals(ReceiverData.ROBBERY_VALUE));
    }

    public void saveRobberyDataFlow(Observable<ReceiverData> observable) {
        observable.subscribe(receiverData -> {
            DataFlowFactory.getSwitchDataFlow()
                    .saveRobberyState(receiverData.getSwitch0Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow()
                    .saveRobberySwitch(CommonParams.HEADLIGHT, receiverData.getSwitch1Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow()
                    .saveRobberySwitch(CommonParams.PARK, receiverData.getSwitch2Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow()
                    .saveRobberySwitch(CommonParams.GUN, receiverData.getSwitch3Value().equals("1"));
        });
    }
}
