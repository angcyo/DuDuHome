package com.dudu.workflow.guard;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.ObservableFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/19.
 */
public class GuardFlow {

    private Logger logger = LoggerFactory.getLogger("GuardFlow");

    public Observable<Boolean> getGuardDataFlow() {
        return ObservableFactory.getReceiverObservable()
                .filter(new Func1<ReceiverData,Boolean>() {
                    @Override
                    public Boolean call(ReceiverData data) {
                        return data.getTitle().equals(ReceiverData.THEFT_VALUE);
                    }
                })
                .map(new Func1<ReceiverData, Boolean>() {
                    @Override
                    public Boolean call(ReceiverData receiverData) {
                        return receiverData.getSwitchValue().equals("1");
                    }
                });
    }
}
