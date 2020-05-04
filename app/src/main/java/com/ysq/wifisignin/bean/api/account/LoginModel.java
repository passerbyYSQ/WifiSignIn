package com.ysq.wifisignin.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 15:57
 */
public class LoginModel {
    @Expose
    private String phone;
    @Expose
    private String password;

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
}
