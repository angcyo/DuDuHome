package com.dudu.workflow.common;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.commonlib.utils.RxBus;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/19.
 */
public class ObservableFactory {

    public static Observable<ReceiverData> getReceiverObservable(){
        return RxBus.getInstance().asObservable()
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object event) {
                        return event instanceof ReceiverData;
                    }
                })
                .map(new Func1<Object, ReceiverData>() {
                    @Override
                    public ReceiverData call(Object event) {
                        return (ReceiverData) event;
                    }
                });
    }
}
