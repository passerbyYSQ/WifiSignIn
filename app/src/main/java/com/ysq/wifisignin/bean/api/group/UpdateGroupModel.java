package com.ysq.wifisignin.bean.api.group;

import com.ysq.wifisignin.bean.db.Group;


/**
 * @author passerbyYSQ
 * @create 2020-04-12 16:55
 */
public class UpdateGroupModel {

    private Integer groupId;

    private String groupName;

    private String enterPassword;

    private String photo;

    private String announcement;

    private String description;

    public UpdateGroupModel(Integer groupId, String groupName, String enterPassword,
                            String photo, String announcement, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.enterPassword = enterPassword;
        this.photo = photo;
        this.announcement = announcement;
        this.description = description;
    }

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
