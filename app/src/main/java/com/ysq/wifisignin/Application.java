package com.ysq.wifisignin;

/**
 * @author passerbyYSQ
 * @create 2020-04-21 21:20
 */
public class Application extends android.app.Application {

    // 维护一个Application实例（单例）
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Factory.setup();
    }

    /**
     * 外部获取单例
     * @return
     */
    public static Application getInstance() {
        return instance;
    }


}
