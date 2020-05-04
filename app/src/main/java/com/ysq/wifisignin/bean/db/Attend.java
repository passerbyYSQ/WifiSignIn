package com.ysq.wifisignin.bean.db;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * 签到的实体类，与数据库表对应
 * @author passerbyYSQ
 * @create 2020-04-16 16:57
 */
public class Attend {
    public static final Integer STATUS_IS_LATE = 0; // 迟到
    public static final Integer STATUS_IN_TIME = 1; // 没有迟到

    @Expose
    private Integer id;
    @Expose
    private Integer userId; // 签到人
    @Expose
    private User user; // 签到人

    @Expose
    private Integer initiateId; // 要签的是哪一次到
    @Expose
    private Initiate initiate;

    @Expose
    private Date time; // 签到的时间
    @Expose
    private Integer status; // 迟到或者没迟到

    public Attend(Integer id, Integer userId,
                  Integer initiateId, Date time, Integer status) {
        this.id = id;
        this.userId = userId;
        this.initiateId = initiateId;
        this.time = time;
        this.status =  status;
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


    public Integer getInitiateId() {
        return initiateId;
    }

    public void setInitiateId(Integer initiateId) {
        this.initiateId = initiateId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Initiate getInitiate() {
        return initiate;
    }

    public void setInitiate(Initiate initiate) {
        this.initiate = initiate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
