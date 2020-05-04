package com.ysq.wifisignin.bean.api.group;

import com.google.gson.annotations.Expose;
import com.ysq.wifisignin.bean.db.Group;


/**
 * @author passerbyYSQ
 * @create 2020-04-12 16:55
 */
public class UpdateGroupModel {
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

    public Group updateToGroup(Group group) {
        group.setGroupName(groupName);
        group.setEnterPassword(enterPassword);
        group.setPhoto(photo);
        group.setAnnouncement(announcement);
        group.setDescription(description);
        return group;
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

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
