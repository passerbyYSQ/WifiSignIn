package com.ysq.wifisignin.bean.api.group;

import com.google.gson.annotations.Expose;

/**
 * @author passerbyYSQ
 * @create 2020-04-12 13:58
 */
public class JoinGroupModel {
    @Expose
    private Integer groupId;
    @Expose
    private String enterPassword;
    @Expose
    private String alias; // 在群里的备注名

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getEnterPassword() {
        return enterPassword;
    }

    public void setEnterPassword(String enterPassword) {
        this.enterPassword = enterPassword;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
