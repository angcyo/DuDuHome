package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.bean.SemanticBean;

/**
 * Created by lxh on 2016/2/16.
 */
public class FaultDefaultChain extends DefaultChain{

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

    private boolean fault(SemanticBean semantic){
        if(semantic.getText().equals(FAULT_CLEAR)||
                semantic.equals(FAULT_PALY)){
            return true;
        }
        return false;
    }
}
