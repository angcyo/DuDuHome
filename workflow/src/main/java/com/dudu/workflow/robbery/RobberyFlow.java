package com.dudu.workflow.robbery;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.common.ObservableFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/19.
 */
public class RobberyFlow {

    private Logger logger = LoggerFactory.getLogger("RobberyFlow");

    private static RobberyFlow mInstance = new RobberyFlow();

    public static RobberyFlow getInstance(){
        return mInstance;
    }

    public Observable<ReceiverData> getRobberyFlow() {
        return ObservableFactory.getReceiverObservable()
                .filter(new Func1<ReceiverData,Boolean>() {
                    @Override
                    public Boolean call(ReceiverData data) {
                        return data.getTitle().equals(ReceiverData.ROBBERY_VALUE);
                    }
                });
    }
}
