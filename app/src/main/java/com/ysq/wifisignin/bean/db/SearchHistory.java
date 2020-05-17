package com.ysq.wifisignin.bean.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * @author passerbyYSQ
 * @create 2020-05-16 10:45
 */
@Table(database = AppDatabase.class)
public class SearchHistory extends BaseModel {

    public static final Integer TYPE_GROUP = 0; // 搜索群

    @PrimaryKey
    @Column
    private String keywords;

    @Column
    private Date updateAt;

    @Column
    private Integer type;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
