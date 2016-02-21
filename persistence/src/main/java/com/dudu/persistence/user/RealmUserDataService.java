package com.dudu.persistence.user;

import com.dudu.persistence.rx.RealmObservable;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/19.
 */
public class RealmUserDataService implements UserDataService {

    public RealmUserDataService() {
    }

    @Override
    public Observable<User> findUser() {
        return RealmObservable.object(new Func1<Realm, RealmUser>() {
            @Override
            public RealmUser call(Realm realm) {
                RealmResults<RealmUser> userList = realm.where(RealmUser.class).findAll();
                if(userList.size()>0) {
                    return userList.first();
                }else{
                    RealmUser realmData = new RealmUser();
                    realmData.setUserName("13800138000");
                    return realmData;
                }
            }
        }).map(new Func1<RealmUser, User>() {
            @Override
            public User call(RealmUser realmUser) {
                return userFromRealm(realmUser);
            }
        });
    }

    @Override
    public Observable<User> saveUser(final User user) {
        return RealmObservable.object(new Func1<Realm, RealmUser>() {
            @Override
            public RealmUser call(Realm realm) {
                RealmUser realmUser = new RealmUser();
                realmUser.setUserName(user.getUserName());
                return realm.copyToRealmOrUpdate(realmUser);
            }
        }).map(new Func1<RealmUser, User>() {
            @Override
            public User call(RealmUser realmUser) {
                // map to UI object
                return new User(realmUser);
            }
        });
    }

    private static User userFromRealm(RealmUser realmUser) {
        User user = new User();
        user.setUserName(realmUser.getUserName());
        user.setId(realmUser.getUserId());
        return user;
    }
}
