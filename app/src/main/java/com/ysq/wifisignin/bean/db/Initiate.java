package com.ysq.wifisignin.bean.db;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * 发起签到的实体类
 * @author passerbyYSQ
 * @create 2020-04-16 15:36
 */
public class Initiate {
    @Expose
    private Integer id;

    @Expose
    private Integer userId; // 发起人的Id
    @Expose
    private User user;

    @Expose
    private Integer groupId; // 在哪个群发起
    @Expose
    private Group group;

    @Expose
    private Date startTime; // 开始时间
    @Expose
    private Date endTime; // 结束时间
    @Expose
    private String macAddress; // wifi的mac地址

    public Initiate(Integer id, Integer userId, Integer groupId, Date startTime,
                    Date endTime, String macAddress) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.macAddress = macAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
