package com.dudu.persistence.user;

/**
 * Created by Administrator on 2016/2/19.
 */
public class User {

    private int id;

    private String userName;

    private String password;

    public User(){

    }

    public User(RealmUser realmUser){
        this.userName = realmUser.getUserName();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
