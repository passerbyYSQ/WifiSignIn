package com.ysq.wifisignin.bean.api.sign;

import com.google.gson.annotations.Expose;
import com.ysq.wifisignin.bean.db.Initiate;

import java.util.Calendar;
import java.util.Date;

/**
 * 请求发起签到的Model
 * @author passerbyYSQ
 * @create 2020-04-16 16:08
 */
public class InitiateModel {
    @Expose
    private Integer groupId; // 在哪个群发起
    @Expose
    private Integer duration; // 时长。单位为：分钟
    @Expose
    private String macAddress; // wifi的mac地址

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
}
