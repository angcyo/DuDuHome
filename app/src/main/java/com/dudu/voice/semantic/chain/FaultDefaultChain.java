package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.carChecking.CarCheckingProxy;
import com.dudu.voice.FloatWindowUtils;
import com.dudu.voice.semantic.bean.SemanticBean;

/**
 * Created by lxh on 2016/2/16.
 */
public class FaultDefaultChain extends DefaultChain {

    public static final String FAULT_CLEAR = "清除故障码";

    public static final String FAULT_PALY = "故障播报";

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        return fault(semantic);
    }

    private boolean fault(SemanticBean semantic) {
        if (!TextUtils.isEmpty(semantic.getText()) && semantic.getText().equals(FAULT_CLEAR)) {

            CarCheckingProxy.getInstance().clearFault();
            FloatWindowUtils.removeFloatWindow();
            return true;
        }
        return false;
    }
}
