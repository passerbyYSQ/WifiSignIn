package com.ysq.wifisignin.bean.db;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 22:12
 */
public class Group {
    @Expose
    private Integer groupId;
    @Expose
    private String groupName;
    @Expose
    private String enterPassword;
    @Expose
    private String photo;
    @Expose
    private String announcement;
    @Expose
    private String description;

    @Expose
    private Integer creatorId;
    @Expose
    private User creator; // 需要的时候再去获取并设置进去

    @Expose
    private Date createAt;
    @Expose
    private Date updateAt;

    public Group() {
    }

    public Group(Integer groupId, String groupName, String photo,
                 String announcement, String description, Integer creatorId,
                 Date createAt, Date updateAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.photo = photo;
        this.announcement = announcement;
        this.description = description;
        this.creatorId = creatorId;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEnterPassword() {
        return enterPassword;
    }

    public void setEnterPassword(String enterPassword) {
        this.enterPassword = enterPassword;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
}
