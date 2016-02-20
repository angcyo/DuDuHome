package com.dudu.workflow.guard;

/**
 * Created by Administrator on 2016/2/16.
 */
public interface GuardRequest {
    public void isAntiTheftOpened(LockStateCallBack callBack);
    public void lockCar(final LockStateCallBack callBack);
    public void unlockCar(final UnlockCallBack callBack);

    public interface LockStateCallBack{
        public void hasLocked(boolean locked);
        public void requestError(String error);
    }

    public interface UnlockCallBack{
        public void unlocked(boolean locked);
        public void requestError(String error);
    }
}
