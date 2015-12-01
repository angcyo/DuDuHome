package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.text.TextUtils;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.CommonAddressType;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.semantic.SemanticConstants;

import org.json.JSONObject;


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
                    NavigationManager.getInstance(mContext).setCommonAddressType(CommonAddressType.HOME);
                    break;
                case CommonAddressUtil.HOMETOWN:
                    address = CommonAddressUtil.getHometown(mContext);
                    location = CommonAddressUtil.getHometownAddress(mContext);
                    NavigationManager.getInstance(mContext).setCommonAddressType(CommonAddressType.HOMETOWN);
                    break;

                case CommonAddressUtil.COMPANY:
                    address = CommonAddressUtil.getCompany(mContext);
                    location = CommonAddressUtil.getCompanyAddress(mContext);
                    NavigationManager.getInstance(mContext).setCommonAddressType(CommonAddressType.COMPANY);
                    break;

            }


            if(!TextUtils.isEmpty(address)&&!checkPoint(location)){
                FloatWindowUtil.removeFloatWindow();
                Navigation navigation = new Navigation(new Point(location[0],location[1]), NaviDriveMode.SPEEDFIRST, NavigationType.NAVIGATION);
                NavigationClerk.getInstance().startNavigation(navigation);

            }else{
                String playText = "您还没有常用的" + addressType + "地址，是否添加？";
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
