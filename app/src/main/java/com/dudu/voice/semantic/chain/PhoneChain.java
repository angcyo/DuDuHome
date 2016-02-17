package com.dudu.voice.semantic.chain;

import android.content.Intent;
import android.text.TextUtils;

import com.dudu.android.launcher.ui.activity.bluetooth.BtOutCallActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.FloatWindowUtils;
import com.dudu.voice.semantic.bean.PhoneBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by 赵圣琪 on 2016/1/6.
 */
public class PhoneChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstant.SERVICE_PHONE.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        PhoneBean bean = (PhoneBean) semantic;

        String contactName = bean.getContactName();
        String phoneNumber = bean.getPhoneNumber();

        Intent intent = new Intent(mContext, BtOutCallActivity.class);
        if (!TextUtils.isEmpty(contactName)) {
            intent.putExtra(Constants.EXTRA_CONTACT_NAME, contactName);
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phoneNumber);
        }

        if (TextUtils.isEmpty(contactName) && TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        FloatWindowUtils.removeFloatWindow();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }

}
