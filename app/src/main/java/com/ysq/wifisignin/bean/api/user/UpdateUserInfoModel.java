package com.ysq.wifisignin.bean.api.user;

import com.google.gson.annotations.Expose;
import com.ysq.wifisignin.bean.db.User;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 16:07
 */
public class UpdateUserInfoModel {
    @Expose
    private String userName;
    @Expose
    private String photo;
    @Expose
    private Integer sex;
    @Expose
    private String description; // 描述可以改为空


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

}
