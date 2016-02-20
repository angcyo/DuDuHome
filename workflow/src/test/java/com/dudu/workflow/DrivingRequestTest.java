package com.dudu.workflow;

import com.dudu.commonlib.CommonLib;
import com.dudu.commonlib.utils.TimeUtils;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.DrivingHabitsData;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.driving.DrivingRequest;
import com.dudu.workflow.switchmessage.AccTestData;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        DataFlowFactory.getInstance().init();
    }

    private int index = 20;

    @Test
    public void test_pushAcceleratedTestDataManyTimes() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            test_pushAcceleratedTestData();
        }
    }

    @Test
    public void test_pushAcceleratedTestData() throws InterruptedException {
        index++;
        final CountDownLatch signal = new CountDownLatch(1);
        AccTestData data = new AccTestData("1", index + ".02", System.currentTimeMillis() + "");
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
    public void test_pushDrivingHabitsDataManyTimes() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        for (int i = 0; i < 20; i++) {
            test_pushDrivingHabitsData();
            signal.await(20, TimeUnit.SECONDS);
        }
    }

    @Test
    public void test_pushDrivingHabitsData() throws InterruptedException {
        index++;
        final CountDownLatch signal = new CountDownLatch(1);
        DrivingHabitsData data = new DrivingHabitsData();
        String value;
        switch (index % 3) {
            case 1:
                value = DrivingHabitsData.JIJIAKE;
                break;
            case 2:
                value = DrivingHabitsData.JIXINGXIA;
                break;
            case 3:
                value = DrivingHabitsData.LECIZHE;
                break;
            default:
                value = DrivingHabitsData.LECIZHE;
                break;
        }
        data.setDriverType(value);
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
