package com.ysq.wifisignin.bean.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.security.Principal;
import java.util.Date;

/**
 * 对应服务端返回的User
 * 同时，对应本地数据库的User表
 * @author passerbyYSQ
 * @create 2020-04-11 10:18
 */
@Table(database = AppDatabase.class)
public class User extends BaseModel {

    public static final Integer FEMALE = 0; // 男性
    public static final Integer MALE = 1; // 女性
    public static final Integer SECRET = 3; // 保密

    @PrimaryKey
    @Column
    private Integer userId;
    @Column
    private String userName;
    @Column
    private String phone; // 手机号
    @Column
    private String photo; // 用户头像
    @Column
    private Integer sex = SECRET;
    @Column
    private String description; // 个人描述

    private String token;

    @Column
    private Date createAt; // 创建时间
    @Column
    private Date updateAt; // 最后一次更新时间


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                ", sex=" + sex +
                ", description='" + description + '\'' +
                ", token='" + token + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
