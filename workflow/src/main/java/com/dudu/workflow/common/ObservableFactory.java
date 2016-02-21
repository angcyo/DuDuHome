package com.dudu.workflow.common;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.commonlib.utils.RxBus;
import com.dudu.workflow.driving.DrivingFlow;
import com.dudu.workflow.guard.GuardFlow;
import com.dudu.workflow.obd.CarCheckFlow;
import com.dudu.workflow.robbery.RobberyFlow;
import com.dudu.workflow.robbery.RobberyStateModel;

import java.io.IOException;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/19.
 */
public class ObservableFactory {
    private static GuardFlow guardFlow = new GuardFlow();
    private static RobberyFlow robberyFlow = new RobberyFlow();
    private static DrivingFlow drivingFlow = new DrivingFlow();
    private static CarCheckFlow carCheckFlow = new CarCheckFlow();

    public static void init() {
        drivingFlow.testAccSpeedFlow(getReceiverObservable());
        guardFlow.saveGuardDataFlow(getGuardReceiveObservable());
        robberyFlow.saveRobberyDataFlow(syncAppRobberyFlow());
    }

    public static Observable<Boolean> getLightErrorObservable(){
        return RxBus.getInstance().asObservable()
                .filter(event ->
                        event instanceof Boolean)
                .map(receivedEvent ->
                        (Boolean) receivedEvent);
    }

    private static Observable<ReceiverData> getReceiverObservable() {
        return RxBus.getInstance().asObservable()
                .filter(event ->
                        event instanceof ReceiverData)
                .map(receiverdataEvent ->
                        (ReceiverData) receiverdataEvent);
    }

    public static Observable<Boolean> getRobberyStateObservable() {
        return RxBus.getInstance().asObservable()
                .filter(event->event instanceof RobberyStateModel)
                .map(receivedData ->
                        (RobberyStateModel)receivedData)
                .map(robberyStateModel ->
                        robberyStateModel.getRobberyState());
    }

    public static Observable<Boolean> getGuardReceiveObservable() {
        return guardFlow.getGuardDataFlow(getReceiverObservable());
    }

    public static Observable<ReceiverData> syncAppRobberyFlow() {
        return robberyFlow.syncAppRobberyFlow(getReceiverObservable());
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
