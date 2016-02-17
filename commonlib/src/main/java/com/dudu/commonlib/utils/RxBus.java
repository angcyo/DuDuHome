package com.dudu.commonlib.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Administrator on 2016/2/17.
 */
public class RxBus {

    private static volatile RxBus mDefaultInstance;

    private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

    private RxBus() {
    }

    public static RxBus getInstance() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    public void send(Object o) {
        mBusSubject.onNext(o);
    }

    public Observable<Object> asObservable() {
        return mBusSubject;
    }
}
