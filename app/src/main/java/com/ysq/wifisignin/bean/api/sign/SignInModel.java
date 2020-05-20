package com.ysq.wifisignin.bean.api.sign;

/**
 * 请求签到的Model
 * @author passerbyYSQ
 * @create 2020-04-16 16:53
 */
public class SignInModel {

    private Integer initiateId; // 要签的是哪一次到？

    private String macAddress; // wifi的mac地址

    public SignInModel(Integer initiateId, String macAddress) {
        this.initiateId = initiateId;
        this.macAddress = macAddress;
    }

    public Integer getInitiateId() {
        return initiateId;
    }

    public void setInitiateId(Integer initiateId) {
        this.initiateId = initiateId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
