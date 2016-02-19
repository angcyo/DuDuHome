package com.dudu.workflow;

import com.dudu.commonlib.CommonLib;
import com.dudu.commonlib.utils.TimeUtils;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.AccTestData;
import com.dudu.rest.model.DrivingHabitsData;
import com.dudu.workflow.common.FlowFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.driving.DrivingRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DrivingRequestTest {

    @Before
    public void setUp() {
        CommonLib.getInstance().init();
//        CommonParams.getInstance().init();
        Request.getInstance().init();
        RequestFactory.getInstance().init();
        FlowFactory.getInstance().init();
    }

    private int index = 20;

    @Test
    public void test_pushManyTimes() throws InterruptedException {
        for(int i=0;i<10;i++){
            test_pushAcceleratedTestData();
        }
    }

    @Test
    public void test_pushAcceleratedTestData() throws InterruptedException {
        index++;
        final CountDownLatch signal = new CountDownLatch(1);
        AccTestData data = new AccTestData();
        data.setAccType("1");
        data.setAccTotalTime(index+".02");
        data.setDateTime(System.currentTimeMillis()+"");
        RequestFactory.getDrivingRequest().pushAcceleratedTestData(data, new DrivingRequest.RequesetCallback() {
            @Override
            public void requestSuccess(boolean success) {
                signal.countDown();
                assertTrue(success);
            }
        });
        signal.await();
    }

    @Test
    public void test_pushDrivingHabitsData() throws InterruptedException {
        index++;
        final CountDownLatch signal = new CountDownLatch(1);
        DrivingHabitsData data = new DrivingHabitsData();
        data.setDriverType(DrivingHabitsData.JIJIAKE);
        data.setTime(TimeUtils.format(TimeUtils.format7));
        data.setDate(TimeUtils.format(TimeUtils.format8));
        RequestFactory.getDrivingRequest().pushDrivingHabitsData(data, new DrivingRequest.RequesetCallback() {
            @Override
            public void requestSuccess(boolean success) {
                signal.countDown();
                assertTrue(success);
            }
        });
        signal.await();
    }

}
