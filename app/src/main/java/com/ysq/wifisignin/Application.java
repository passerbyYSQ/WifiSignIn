package com.ysq.wifisignin;

import android.os.SystemClock;

import java.io.File;

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


    /**
     * 得到缓存文件夹的地址
     * @return  当前APP的缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时存储文件地址
     * @return
     */
    public static File getPortraitTmpFile() {
        // 得到头像目录的缓存地址
        File dir = new File(getCacheDirFile(), "portrait");
        // 创建所有的对应的文件夹
        dir.mkdirs();

        // 删除旧的缓存文件
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        // 返回一个当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }


}
