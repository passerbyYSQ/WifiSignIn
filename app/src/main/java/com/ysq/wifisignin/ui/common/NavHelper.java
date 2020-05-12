package com.ysq.wifisignin.ui.common;

import android.content.Context;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * 完成对Fragment的调度与重用问题，
 * 达到最优的Fragment切换
 * T：Tab中的额外字段的类型
 */
public class NavHelper<T> {
    // 所有的Tab集合
    private SparseArray<Tab<T>> tabs = new SparseArray<>();
    // 当前的Tab（选中的Tab）
    private Tab<T> currentTab;

    // 用于初始化的必须参数
    private FragmentManager fragmentManager;
    private int containerId;
    private Context context;
    private OnTabChangedListener<T> listener;

    public NavHelper(Context context, int containerId,
                     FragmentManager fragmentManager,
                     OnTabChangedListener<T> listener) {

        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 添加Tab
     * @param menuId    Tab对应的菜单id
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;  // 流式添加
    }

    /**
     * 获取当前的Tab
     * @return
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param menuId    菜单的Id
     * @return          能否处理这个点击
     */
    public boolean performClickMenu(int menuId) {
        // 在集合中寻找点击的菜单的对应的Tab，如果有，则处理
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false; // 不能够处理的情况
    }

    /**
     * 进行真实的Tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;

        if (currentTab != null) {
            oldTab = currentTab;

            // 如果当前的tab等于旧的tab（重复点击），就不做处理
            if (tab == oldTab) {
                notifyTabReselect(tab); // 刷新操作
                return;
            }
        }
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    /**
     * 进行Fragment的真实的调度
     * @param newTab
     * @param oldTab
     */
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (oldTab != null) {
            if (oldTab.fragment != null) {
                // 将旧的Fragment从界面上移除（内存上没有销毁）
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                // 首次新建
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                // 缓存起来
                newTab.fragment = fragment;
                // 提交到FragmentManager中
                ft.add(containerId, fragment, newTab.clx.getName());
            } else {
                // 将fragment从FragmentManager的缓存空间中拿出来再次加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        // 提交事务
        ft.commit();
        // 通知回调
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * 回调我们的监听器
     * @param newTab
     * @param oldTab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab) {
        // TODO 重复点击Tab所做的操作
    }

    /**
     * 我们所有的Tab基础属性
     * @param <T>   泛型的额外参数
     */
    public static class Tab<T> {
        // Fragment对应的Class信息
        public Class<?> clx;
        // 额外的字段，用户自己设定需要使用
        public T extra;
        // 内部缓存的对应的Fragment，
        Fragment fragment;

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
    }

    /**
     * 定义事件处理完成之后的回调接口
     * @param <T>
     */
    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }

}
