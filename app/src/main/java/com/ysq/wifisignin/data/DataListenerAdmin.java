package com.ysq.wifisignin.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 全局的所有数据监听回调的管理者
 *
 * @author passerbyYSQ
 * @create 2020-05-14 1:13
 */
public class DataListenerAdmin {

    private static final DataListenerAdmin instance;

    /**
     * 观察者集合
     * Class<?>                 key，是被观察者
     * Set<ChangedListener>     对应的观察者（监听者），可能多有个
     */
    private static final Map<Class, Set<ChangedListener>> changedListeners = new HashMap<>();

    // 单例
    static {
        instance = new DataListenerAdmin();
    }

    private DataListenerAdmin() {
    }

    /**
     * 给某个被观察者注册监听器
     * @param tClass        被观察者
     * @param listener      监听器
     * @param <DataType>    所要回调的数据的类型
     */
    public static <DataType> void addChangedListener(final Class tClass,
                                                     ChangedListener<DataType> listener) {
        Set<ChangedListener> listeners = instance.getListeners(tClass);
        if (listeners == null) {
            // 初始化容器
            listeners = new HashSet<>();
            // 添加到Map中
            instance.changedListeners.put(tClass, listeners);

        }
        listeners.add(listener);
    }

    /**
     * 给某个被观察者，移除某个监听器
     * @param tClass
     * @param listener
     * @param <DataType>
     */
    public static <DataType> void removeChangedListener(final Class tClass,
                                                        ChangedListener<DataType> listener) {
        Set<ChangedListener> listeners = instance.getListeners(tClass);
        if (listeners == null) {
            return;
        }
        // 从容器中删除这个监听者
        listeners.remove(listener);
    }

    /**
     * 给某个被观察者的所有观察者，做统一的通知分发
     * @param tClass        被观察者
     * @param action        数据更改的类型
     * @param dataList      所要回调的数据集合
     * @param <DataType>
     */
    public static <DataType> void notifyChanged(final Class tClass, int action,
                                                final DataType... dataList) {
        // 找监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            // 通用的通知
            for (ChangedListener<DataType> listener : listeners) {
                listener.onDataChanged(action, dataList);
            }
        }
    }

    /**
     * 得到某个被观察者的所有观察者
     * @param tClass
     * @return
     */
    private static Set<ChangedListener> getListeners(Class tClass) {
        if (changedListeners.containsKey(tClass)) {
            return changedListeners.get(tClass);
        }
        return null;
    }

    public interface ChangedListener<DataType> {
        int ACTION_ADD = 0;
        int ACTION_UPDATE = 1;
        int ACTION_DELETE = 2;

        void onDataChanged(int action, DataType... dataList);
    }
}
