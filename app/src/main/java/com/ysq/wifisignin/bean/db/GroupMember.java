package com.ysq.wifisignin.bean.db;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * @author passerbyYSQ
 * @create 2020-04-11 22:42
 */
public class GroupMember {
    public static final Integer PERMISSION_COMMON = 0;
    public static final Integer PERMISSION_ADMIN = 1;
    public static final Integer PERMISSION_CREATOR = 2;

    @Expose
    private Integer id;

    @Expose
    private Integer userId;
    @Expose
    private User user; // 需要的时候再设置值

    @Expose
    private Integer groupId;
    @Expose
    private Group group; // 需要的时候再设置值

    @Expose
    private String alias; // 用户在群里的备注名
    @Expose
    private Integer permission = PERMISSION_COMMON; // 权限
    @Expose
    private Date createAt; // 我加入的时间
    @Expose
    private Date updateAt;

    public GroupMember(Integer id, Integer userId, Integer groupId, String alias,
                       Integer permission, Date createAt, Date updateAt) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.alias = alias;
        this.permission = permission;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
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
