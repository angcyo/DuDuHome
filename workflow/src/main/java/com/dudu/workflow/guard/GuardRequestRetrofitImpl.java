package com.dudu.workflow.guard;

import com.dudu.rest.common.Request;
import com.dudu.rest.model.GuardStateResponse;
import com.dudu.rest.model.RequestResponse;
import com.dudu.workflow.common.CommonParams;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/2/16.
 */
public class GuardRequestRetrofitImpl implements GuardRequest {

    public static GuardRequestRetrofitImpl mInstance = new GuardRequestRetrofitImpl();

    public static GuardRequestRetrofitImpl getInstance() {
        return mInstance;
    }

    @Override
    public void isAntiTheftOpened(final LockStateCallBack callBack) {
        Call<GuardStateResponse> call = Request.getInstance().getGuardService()
                .getGuardState(CommonParams.getInstance().getUserName());
        call.enqueue(new Callback<GuardStateResponse>() {
            @Override
            public void onResponse(Call<GuardStateResponse> call, Response<GuardStateResponse> response) {
                callBack.hasLocked(response.body().switchValue == 1);
            }

            @Override
            public void onFailure(Call<GuardStateResponse> call, Throwable t) {
                callBack.requestError(t.toString());
            }
        });
    }

    @Override
    public void lockCar(final LockStateCallBack callBack) {
        Call<RequestResponse> call = Request.getInstance().getGuardService()
                .guardSwitch(CommonParams.getInstance().getUserName(),1);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callBack.hasLocked(response.body().resultCode == 0);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                callBack.requestError(t.toString());
            }
        });
    }

    @Override
    public void unlockCar(final UnlockCallBack callBack) {
        Call<RequestResponse> call = Request.getInstance().getGuardService()
                .guardSwitch(CommonParams.getInstance().getUserName(),0);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callBack.unlocked(response.body().resultCode ==0);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                callBack.requestError(t.toString());
            }
        });
    }
}
