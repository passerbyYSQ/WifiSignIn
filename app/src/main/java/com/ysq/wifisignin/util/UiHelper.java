package com.ysq.wifisignin.util;

import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.mylhyl.circledialog.CircleDialog;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.R;

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

    // 显示一个提示框
    public static void showMsgDialog(String title, String msg, FragmentManager fragmentManager) {
        new CircleDialog.Builder()
                .setTitle(title)
                .setText(msg)//内容
                .setTextColor(Application.getInstance().getResources()
                        .getColor(R.color.textSecond))
                .setPositiveInput("确定", null)
                .setPositive("取消", null)
                .show(fragmentManager);
    }
}
