package com.dudu.workflow.robbery;

import com.dudu.rest.common.Request;
import com.dudu.rest.model.QueryRobberyResponse;
import com.dudu.rest.model.RequestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/2/16.
 */
public class RobberyRequestRetrofitImpl implements RobberyRequest{

    public static RobberyRequestRetrofitImpl mInstance = new RobberyRequestRetrofitImpl();

    public static RobberyRequestRetrofitImpl getInstance() {
        return mInstance;
    }

    @Override
    public void getCarInsuranceAuthState(String cellphone) {

    }

    @Override
    public void requestCarInsuranceAuth() {

    }

    @Override
    public void isCarRobbed(String cellphone, final CarRobberdCallback callback) {
        Call<QueryRobberyResponse> call = Request.getInstance().getRobberyService()
                .getRobberyState(cellphone);
        call.enqueue(new Callback<QueryRobberyResponse>() {
            @Override
            public void onResponse(Call<QueryRobberyResponse> call, Response<QueryRobberyResponse> response) {
                callback.hasRobbed(response.body().switch0==1);
            }

            @Override
            public void onFailure(Call<QueryRobberyResponse> call, Throwable t) {
                callback.requestError(t.toString());
            }
        });
    }


    @Override
    public void settingAntiRobberyMode(String cellphone, int type, int on_off, final SwitchCallback callback) {
        Call<RequestResponse> call = Request.getInstance().getRobberyService()
                .robberySwitch(cellphone, type, on_off);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callback.switchSuccess(response.body().resultCode == 0);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                callback.requestError(t.getMessage().toString());
            }
        });
    }

    @Override
    public void closeAntiRobberyMode(String cellphone, final CloseRobberyModeCallback callback) {
        Call<RequestResponse> call = Request.getInstance().getRobberyService()
                .robberySwitch(cellphone, 0, 0);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callback.closeSuccess(response.body().resultCode==0);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                callback.requestError(t.getMessage().toString());
            }
        });
    }

    @Override
    public void getRobberyState(String cellphone, final RobberStateCallback callback) {
        Call<QueryRobberyResponse> call = Request.getInstance().getRobberyService()
                .getRobberyState(cellphone);
        call.enqueue(new Callback<QueryRobberyResponse>() {
            @Override
            public void onResponse(Call<QueryRobberyResponse> call, Response<QueryRobberyResponse> response) {
                callback.switchsState(response.body().switch1==1,response.body().switch2==1,response.body().switch3==1);
            }

            @Override
            public void onFailure(Call<QueryRobberyResponse> call, Throwable t) {
                callback.requestError(t.toString());
            }
        });
    }

}

