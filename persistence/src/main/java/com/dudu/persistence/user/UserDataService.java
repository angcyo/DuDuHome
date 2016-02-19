package com.dudu.persistence.user;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/19.
 */
public interface UserDataService {
    Observable<User> findUser();
    Observable<User> saveUser(User username);
}
