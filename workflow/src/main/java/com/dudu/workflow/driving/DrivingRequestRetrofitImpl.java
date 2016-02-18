package com.dudu.workflow.driving;

import com.dudu.commonlib.utils.Encrypt;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.AccTestData;
import com.dudu.rest.model.RequestResponse;
import com.dudu.workflow.CommonParams;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DrivingRequestRetrofitImpl implements DrivingRequest{
    public static DrivingRequestRetrofitImpl mInstance = new DrivingRequestRetrofitImpl();

    public static DrivingRequestRetrofitImpl getInstance() {
        return mInstance;
    }

    @Override
    public void pushAcceleratedTestData(AccTestData time, final RequesetCallback callback) {
        String json = new Gson().toJson(time);
        System.out.println(json);
        String postString;
        try {
            postString=Encrypt.AESEncrypt(json, Encrypt.vi);
        }catch (Exception e){
            postString = json;
            System.out.println(e);
        }
        Call<RequestResponse> call = Request.getInstance().getDrivingService()
                .pushAcceleratedTestData(CommonParams.getInstance().getUserName(), postString);
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
}
