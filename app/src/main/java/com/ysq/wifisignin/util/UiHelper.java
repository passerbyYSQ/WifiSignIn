package com.ysq.wifisignin.util;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.ysq.wifisignin.Application;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * @author passerbyYSQ
 * @create 2020-05-07 15:46
 */
public class UiHelper {

    private static Application instance;

    static {
        instance = Application.getInstance();
    }

    /**
     * 显示一个提示
     * @param msg
     */
    public static void showToast(final String msg) {
        // Toast只能在主线程中显示，所以需要进行线程转换，保证一定是在主线程进行的show操作
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候一定就是主线程状态
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }

}