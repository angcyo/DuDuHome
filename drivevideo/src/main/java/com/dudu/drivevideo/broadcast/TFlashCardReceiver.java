package com.dudu.drivevideo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dudu.commonlib.CommonLib;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by 赵圣琪 on 2016/1/3.
 */
public class TFlashCardReceiver extends BroadcastReceiver {

    private static PublishSubject<String> tFlashCardActionSubject = PublishSubject.create();

    public static Observable<String> getTFlashCardAction(){
        return tFlashCardActionSubject.asObservable();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        tFlashCardActionSubject.onNext(action);

        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {

        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {

        }
    }

    public void registReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        CommonLib.getInstance().getContext().registerReceiver(this, intentFilter);
    }

    public void unregistReceiver(){
        CommonLib.getInstance().getContext().unregisterReceiver(this);
    }
}
