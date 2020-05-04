package com.ysq.wifisignin.ui.common;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储所有已创建的Activity
 * @author passerbyYSQ
 * @create 2020-03-05 23:25
 */
public class ActivityCollector {
    // 容器，存储所有已创建的Activity的引用
    private static List<Activity> activities = new ArrayList<>();

    // 在Activity创建的时候调用该方法，将其引用存起来
    public static void add(Activity activity) {
        activities.add(activity);
    }

    // 在Activity销毁的时候调用该方法，将其引用移除
    public static void remove(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            // 如果还没finish掉
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        // 清空集合
        activities.clear();
    }
}
