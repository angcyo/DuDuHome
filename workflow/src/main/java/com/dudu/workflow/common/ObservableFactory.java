package com.dudu.workflow.common;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.workflow.driving.DrivingFlow;
import com.dudu.workflow.obd.CarCheckFlow;
import com.dudu.workflow.robbery.RobberyFlow;

import java.io.IOException;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class ObservableFactory {
    private static RobberyFlow robberyFlow = new RobberyFlow();
    private static DrivingFlow drivingFlow = new DrivingFlow();
    private static CarCheckFlow carCheckFlow = new CarCheckFlow();

    public static void init() {
    }

    public static void testAccSpeedFlow(ReceiverData receiverData){
        drivingFlow.testAccSpeedFlow(Observable.just(receiverData));
    }

    public static Observable<Boolean> gun3Toggle() throws IOException {
        return robberyFlow.gun3Toggle();
    }

    public static Observable<String> engineFailed() throws IOException {
        return carCheckFlow.engineFailed();
    }

    public static Observable<String> gearboxFailed() throws IOException {
        return carCheckFlow.gearboxFailed();
    }

    public static Observable<String> ABSFailed() throws IOException {
        return carCheckFlow.ABSFailed();
    }

    public static Observable<String> SRSFailed() throws IOException {
        return carCheckFlow.SRSFailed();
    }

    public static Observable<String> WSBFailed() throws IOException {
        return carCheckFlow.WSBFailed();
    }

}
