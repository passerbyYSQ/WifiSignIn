package com.ysq.wifisignin.bean.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 数据库的基本信息
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "wifi_sign_in";
    // 在db包下新增了文件，更改VERSION后会自动重新生成对应的表
    public static final int VERSION = 2;

}
