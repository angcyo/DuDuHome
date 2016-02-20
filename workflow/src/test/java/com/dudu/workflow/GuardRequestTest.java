package com.dudu.workflow;

import com.dudu.commonlib.CommonLib;
import com.dudu.rest.common.Request;
import com.dudu.workflow.common.DataFlowFactory;
import com.dudu.workflow.common.RequestFactory;
import com.dudu.workflow.guard.GuardRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Administrator on 2016/2/16.
 */
public class GuardRequestTest {

    @Before
    public void setUp() {
        CommonLib.getInstance().init();
        Request.getInstance().init();
        RequestFactory.getInstance().init();
        DataFlowFactory.getInstance().init();
    }

    @Test
    public void test_lock() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);
        RequestFactory.getInstance().getGuardRequest()
                .lockCar(new GuardRequest.LockStateCallBack() {

                    @Override
                    public void hasLocked(boolean locked) {
                        signal.countDown();
                        assertTrue(locked);
                    }

                    @Override
                    public void requestError(String error) {
                        signal.countDown();
                        assertNull(error);
                    }
                });
        signal.await();
    }

    @Test
    public void test_unlock() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);
        RequestFactory.getInstance().getGuardRequest()
                .unlockCar(new GuardRequest.UnlockCallBack() {

                    @Override
                    public void unlocked(boolean locked) {
                        signal.countDown();
                        assertTrue(locked);
                    }

                    @Override
                    public void requestError(String error) {
                        signal.countDown();
                        assertNull(error);
                    }
                });
        signal.await();
    }

    @Test
    public void test_isAntiTheftOpened() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);
        RequestFactory.getInstance().getGuardRequest()
                .isAntiTheftOpened(new GuardRequest.LockStateCallBack() {

                    @Override
                    public void hasLocked(boolean locked) {
                        signal.countDown();
                        assertTrue(locked);
                    }

                    @Override
                    public void requestError(String error) {
                        signal.countDown();
                        assertNull(error);
                    }
                });
        signal.await();
    }
}
