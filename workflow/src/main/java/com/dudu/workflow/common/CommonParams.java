package com.dudu.workflow.common;

import com.dudu.persistence.user.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/2/16.
 */
public class CommonParams {
    public static final int ROBBERYSTATE = 0;
    public static final int HEADLIGHT = 1;
    public static final int PARK = 2;
    public static final int GUN = 3;
    private static CommonParams mInstance = new CommonParams();

    private Logger logger = LoggerFactory.getLogger("CommonParams");

    private User user = new User();

    public static CommonParams getInstance() {
        return mInstance;
    }

    public void init() {
        DataFlowFactory.getUserDataFlow().getUserInfo()
                .subscribeOn(Schedulers.newThread())
                .subscribe(user -> {
                    CommonParams.this.user.setId(user.getId());
                    CommonParams.this.user.setUserName(user.getUserName());
                }, error->{
                    logger.error("init", error);
                });
    }

    public User getUser() {
        if (user == null) {
            user = new User();
            user.setId(1);
        }
        return user;
    }

    public String getUserName() {
        return user.getUserName();
    }
}

