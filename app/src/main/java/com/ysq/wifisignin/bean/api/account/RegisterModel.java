package com.ysq.wifisignin.bean.api.account;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 14:19
 */
public class RegisterModel {
    private String phone;
    private String userName;
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
