package com.dudu.workflow;

import com.dudu.commonlib.CommonLib;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.AccTestData;
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

    @Test
    public void test_pushManyTimes() throws InterruptedException {
        for(int i=0;i<100;i++){
            test_pushAcceleratedTestData();
        }
    }

    @Test
    public void test_pushAcceleratedTestData() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);
        AccTestData data = new AccTestData();
        data.setAccType(1);
        data.setAccTotalTime(20.02);
        data.setDateTime(System.currentTimeMillis());
        RequestFactory.getDrivingRequest().pushAcceleratedTestData(data, new DrivingRequest.RequesetCallback() {
            @Override
            public void requestSuccess(boolean success) {
                signal.countDown();
                assertTrue(success);
            }
        });
        signal.await();
    }

}
