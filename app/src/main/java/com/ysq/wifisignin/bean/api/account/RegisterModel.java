package com.ysq.wifisignin.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 14:19
 */
public class RegisterModel {
    @Expose
    private String phone;
    @Expose
    private String userName;
    @Expose
    private String password;

    public RegisterModel(String phone, String userName, String password) {
        this.phone = phone;
        this.userName = userName;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
