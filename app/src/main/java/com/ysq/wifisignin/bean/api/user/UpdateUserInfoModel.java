package com.ysq.wifisignin.bean.api.user;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 16:07
 */
public class UpdateUserInfoModel {

    private String userName;
    private String photo;
    private Integer sex;
    private String description; // 描述可以改为空

    public UpdateUserInfoModel(String userName, String photo, Integer sex, String description) {
        this.userName = userName;
        this.photo = photo;
        this.sex = sex;
        this.description = description;
    }

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
