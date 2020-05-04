package com.ysq.wifisignin.bean.api.group;

import com.google.gson.annotations.Expose;
import com.ysq.wifisignin.bean.db.Group;


/**
 * @author passerbyYSQ
 * @create 2020-04-11 22:07
 */
public class CreateGroupModel {
    @Expose
    private String groupName;
    @Expose
    private String enterPassword; // 加群密码
    @Expose
    private String photo; // 群头像
    @Expose
    private String description; // 群描述
    @Expose
    private String alias; // 在群里的备注名


    public Group buildGroup() {
        Group group = new Group();
        group.setGroupName(groupName);
        group.setEnterPassword(enterPassword);
        group.setPhoto(photo);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
