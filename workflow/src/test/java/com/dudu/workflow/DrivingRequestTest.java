package com.dudu.workflow;

import com.dudu.commonlib.CommonLib;
import com.dudu.rest.common.Request;
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
        RequestFactory.getInstance().init();
        Request.getInstance().init();
    }

    @Test
    public void test_pushAcceleratedTestData() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);
        RequestFactory.getDrivingRequest().pushAcceleratedTestData(20.02, new DrivingRequest.RequesetCallback() {
            @Override
            public void requestSuccess(boolean success) {
                signal.countDown();
                assertTrue(success);
            }
        });
        signal.await();
    }

}
