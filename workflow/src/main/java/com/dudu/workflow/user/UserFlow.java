package com.dudu.workflow.user;

import com.dudu.persistence.user.User;
import com.dudu.persistence.user.UserDataService;
import com.dudu.workflow.common.CommonParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/16.
 */
public class UserFlow {

    private static final String TAG = "UserFlow";

    private Logger logger = LoggerFactory.getLogger(TAG);

    private UserDataService userDataService;

    public UserFlow(UserDataService userDataService){
        this.userDataService = userDataService;
    }

    public void saveUserName(String userName){
        User user = CommonParams.getInstance().getUser();
        user.setUserName(userName);
        userDataService.saveUser(user)
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        logger.debug(user.getUserName()+"保存成功");
                    }
                }, error->{
                    logger.error("saveUserName", error);
                });
    }

    public Observable<String> getUserName(){
        return userDataService.findUser()
                .map(new Func1<User, String>() {
                    @Override
                    public String call(User user) {
                        return user.getUserName();
                    }
                });
    }

    public Observable<String> getPassword(){
        return userDataService.findUser()
                .map(new Func1<User, String>() {
                    @Override
                    public String call(User user) {
                        return user.getPassword();
                    }
                });
    }

    public Observable<User> getUserInfo(){
        return userDataService.findUser();
    }
}
