package com.ysq.wifisignin.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ysq.wifisignin.Application;

/**
 * @author passerbyYSQ
 * @create 2020-05-21 16:28
 */
public class HeaderSetting {
    public static final String SPF_NAME = HeaderSetting.class.getSimpleName();

    public static final String KEY_OPTION = "KEY_OPTION";
    public static final String KEY_URL = "KEY_URL";

    public static final int OPTION_DEFAULT = 0;
    public static final int OPTION_BING = 1;
    public static final int OPTION_CUSTOM = 2;

    private static SharedPreferences sp;

    // 哪种设置
    private static int option;
    // 如果不是系统默认，则url不应该为空
    private static String headerUrl;

    /**
     * 存储到XML文件，持久化
     */
    public static void save(int option, String headerUrl) {
        // 修改内存中的数据
        HeaderSetting.option = option;
        HeaderSetting.headerUrl = headerUrl;

        // 获取数据持久化的SharedPreferences
        sp = Application.getInstance()
                .getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);

        // 存储数据
        sp.edit()
            .putInt(KEY_OPTION, option)
            .putString(KEY_URL, headerUrl)
            .apply();
    }

    /**
     * 将配置信息加载到内存中
     */
    public static void load() {
        // 获取数据持久化的SharedPreferences
        sp = Application.getInstance()
                .getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);

        // 获取不到就返回0（表示系统默认）
        option = sp.getInt(KEY_OPTION, 0);
        headerUrl = sp.getString(KEY_URL, "");
    }

    public static int getOption() {
        return option;
    }

    public static String getHeaderUrl() {
        return headerUrl;
    }

}
