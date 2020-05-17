package com.ysq.wifisignin.bean.api.group;

/**
 * @author passerbyYSQ
 * @create 2020-04-12 13:58
 */
public class JoinGroupModel {

    private Integer groupId;

    private String enterPassword;

    private String alias; // 在群里的备注名

    public JoinGroupModel(Integer groupId, String enterPassword, String alias) {
        this.groupId = groupId;
        this.enterPassword = enterPassword;
        this.alias = alias;
    }

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
