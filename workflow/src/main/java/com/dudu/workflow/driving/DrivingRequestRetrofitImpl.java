package com.dudu.workflow.driving;

import com.dudu.commonlib.utils.Encrypt;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.DrivingHabitsData;
import com.dudu.rest.model.RequestResponse;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.switchmessage.AccTestData;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 驾车相关请求Retrofit实现
 * Created by Eaway on 2016/2/17.
 */
public class DrivingRequestRetrofitImpl implements DrivingRequest{
    public static DrivingRequestRetrofitImpl mInstance = new DrivingRequestRetrofitImpl();

    public static DrivingRequestRetrofitImpl getInstance() {
        return mInstance;
    }

    @Override
    public void pushAcceleratedTestData(AccTestData accTestData, final RequesetCallback callback) {
        String json = new Gson().toJson(accTestData);
        System.out.println(json);
        String postString;
        try {
            postString=Encrypt.AESEncrypt(json, Encrypt.vi);
        }catch (Exception e){
            postString = json;
            System.out.println(e);
        }
        Call<RequestResponse> call = Request.getInstance().getDrivingService()
                .pushAcceleratedTestData(CommonParams.getInstance().getUserName()
                        , postString);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callback.requestSuccess(true);
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                callback.requestSuccess(false);
            }
        });
    }

    @Override
    public void pushDrivingHabitsData(DrivingHabitsData drivingHabitsData, final RequesetCallback callback) {
        String json = new Gson().toJson(drivingHabitsData);
        System.out.println(json);
        String postString;
        try {
            postString=Encrypt.AESEncrypt(json, Encrypt.vi);
        }catch (Exception e){
            postString = json;
            System.out.println(e);
        }
        Call<RequestResponse> call = Request.getInstance().getDrivingService()
                .pushDrivingHabits(CommonParams.getInstance().getUserName(), postString);
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                callback.requestSuccess(true);
                System.out.print(response.body().toString());
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                System.out.print(toString());
                callback.requestSuccess(false);
            }
        });
    }
}
