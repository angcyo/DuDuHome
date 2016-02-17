package com.dudu.workflow.driving;

import com.dudu.commonlib.utils.DataJsonTranslation;
import com.dudu.rest.common.Request;
import com.dudu.rest.model.RequestResponse;

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
    public void pushAcceleratedTestData(double time, final RequesetCallback callback) {
        Call<RequestResponse> call = Request.getInstance().getDrivingService()
                .pushAcceleratedTestData("13800138000", DataJsonTranslation.objectToJson(time));
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
