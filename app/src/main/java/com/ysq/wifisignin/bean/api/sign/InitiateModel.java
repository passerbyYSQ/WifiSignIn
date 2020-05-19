package com.ysq.wifisignin.bean.api.sign;

import com.ysq.wifisignin.bean.db.Initiate;

import java.util.Calendar;
import java.util.Date;

/**
 * 请求发起签到的Model
 * @author passerbyYSQ
 * @create 2020-04-16 16:08
 */
public class InitiateModel {

    private Integer groupId; // 在哪个群发起

    private Integer duration; // 时长。单位为：分钟

    private String macAddress; // wifi的mac地址

    public InitiateModel(Integer groupId, Integer duration, String macAddress) {
        this.groupId = groupId;
        this.duration = duration;
        this.macAddress = macAddress;
    }

    public Initiate updateToInitiate(Integer userId) {
        Calendar calendar = Calendar.getInstance();
        Date startTime = calendar.getTime();
        // 计算结束时间
        calendar.add(Calendar.MINUTE, duration);
        Date endTime = calendar.getTime();

        return new Initiate(null, userId, groupId, startTime, endTime, macAddress);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "InitiateModel{" +
                "groupId=" + groupId +
                ", duration=" + duration +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }
}
