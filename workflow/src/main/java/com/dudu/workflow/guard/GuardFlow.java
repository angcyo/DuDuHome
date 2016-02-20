package com.dudu.workflow.guard;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.DataFlowFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class GuardFlow {

    private Logger logger = LoggerFactory.getLogger("GuardFlow");

    public Observable<Boolean> getGuardDataFlow(Observable<ReceiverData> observable) {
        return observable
                .filter(data -> data.getTitle().equals(ReceiverData.THEFT_VALUE))
                .map(receiverData -> receiverData.getSwitchValue().equals("1"));
    }

    public void saveGuardDataFlow(Observable<Boolean> observable) {
        observable.subscribe(locked -> {
                    DataFlowFactory.getSwitchDataFlow()
                            .saveRobberyState(locked);

                });
    }
}
