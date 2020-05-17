package com.ysq.wifisignin.bean.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Date;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 22:12
 */
public class Group extends BaseModel
        implements Serializable {

    private Integer groupId;
    private String groupName;
    private String enterPassword;
    private String photo;
    private String announcement;
    private String description;

    private Integer creatorId;
    private User creator; // 需要的时候再去获取并设置进去

    private Date createAt;
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

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", enterPassword='" + enterPassword + '\'' +
                ", photo='" + photo + '\'' +
                ", announcement='" + announcement + '\'' +
                ", description='" + description + '\'' +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
