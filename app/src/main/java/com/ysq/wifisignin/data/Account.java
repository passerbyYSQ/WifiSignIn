package com.ysq.wifisignin.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.Factory;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.bean.db.User_Table;

/**
 * @author passerbyYSQ
 * @create 2020-04-21 21:25
 */
public class Account {
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";

    // 登录状态的token，用来接口请求
    private static String token;
    // 登录的用户Id
    private static Integer userId;
    // 登录的账户（phone）
    private static String phone;

    /**
     * 存储到XML文件，持久化
     */
    private static void save(Context context) {
        // 获取数据持久化的SharedPreferences
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);

        // 存储数据
        sp.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, phone)
                .apply();
    }

    /**
     * 进行数据加载
     * @param context
     */
    public static void load(Context context) {
        // 获取数据持久化的SharedPreferences
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);

        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getInt(KEY_USER_ID, 0);
        phone = sp.getString(KEY_ACCOUNT, "");

    }

    /**
     * 返回当前账号是否登录
     * @return  true：已登录
     */
    public static boolean isLogin() {
        // 用户Id和Token不为空表示已经登录
        return userId != null
                && !TextUtils.isEmpty(token)
                && !TextUtils.isEmpty(phone);
    }


    /**
     * 保存我自己的信息到持久化XML中
     * @param self
     */
    public static void login(User self) {
        // 存储当前登录的账户，token，用户Id，方便从数据库中查询我的信息
        Account.token = self.getToken();
        Account.phone = self.getPhone();
        Account.userId = self.getUserId();
        save(Application.getInstance());
    }


    /**
     * 获取当前登录的Token
     * @return
     */
    public static String getToken() {
        return token;
    }

    /**
     * 获取当前登录的用户信息
     * @return
     */
    public static User getUser() {
        // 如果为null，返回一个新new的User，否则从数据库查询
        return userId == 0 ? new User() :
                SQLite.select()
                        .from(User.class)
                        .where(User_Table.userId.eq(userId))
                        .querySingle();
    }
}
