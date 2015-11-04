package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/2.
 */
public class CommonAddressChain extends SemanticChain{

    private static final String TAG = "CommonAddressChain";

    private String addressType;

    private String address;



    private double[] location;

    private Context mContext;

    public CommonAddressChain(){

        this.mContext = LauncherApplication.getContext().getApplicationContext();
    }

    @Override
    public boolean matchSemantic(String service) {
        return service.equalsIgnoreCase(SemanticConstants.SERVICE_COMMONADDRESS);
    }

    @Override
    public boolean doSemantic(String json) {

        getAddressType(json);
        if(!TextUtils.isEmpty(addressType)){

            switch (addressType){

                case CommonAddressUtil.HOME:
                    address = CommonAddressUtil.getHome(mContext);
                    location = CommonAddressUtil.getHomeAddress(mContext);
                    MapManager.getInstance().setCommonAddressType(CommonAddressUtil.HOME);
                    break;
                case CommonAddressUtil.HOMETOWN:
                    address = CommonAddressUtil.getHometown(mContext);
                    location = CommonAddressUtil.getHometownAddress(mContext);
                    MapManager.getInstance().setCommonAddressType(CommonAddressUtil.HOMETOWN);
                    break;

                case CommonAddressUtil.COMPANY:
                    address = CommonAddressUtil.getCompany(mContext);
                    location = CommonAddressUtil.getCompanyAddress(mContext);
                    MapManager.getInstance().setCommonAddressType(CommonAddressUtil.COMPANY);
                    break;

            }

            if(!TextUtils.isEmpty(address)&&checkPoint(location)){
                Log.d(TAG,"---------address:" + address + "===locaton:" + location[0] + "   " + location[1]);
                EventBus.getDefault().post(new Navigation(location,Navigation.NAVI_TWO, AMapNavi.DrivingDefault));

            }else{
                String playText = "��û�г��õ�" + addressType + "��ַ���Ƿ���ӣ�";
                mVoiceManager.startSpeaking(playText,SemanticConstants.TTS_START_UNDERSTANDING,true);
            }

            return true;
        }

        return false;
    }

    private String getAddressType(String json){

        try{

            JSONObject semnticObject = new JSONObject(JsonUtils.parseIatResult(json,
                    "semantic"));

            addressType = semnticObject.getJSONObject("slots").getJSONObject("address").getString("type");

        }catch (Exception e){
            addressType = "";
            e.printStackTrace();
        }


        return addressType;
    }



    private boolean checkPoint(double[] point){

        return ((point[0]==-90)&&(point[1]==-90));
    }

}
