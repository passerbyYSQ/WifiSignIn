package com.ysq.wifisignin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.ysq.wifisignin.data.Account;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程池
 */
public class Factory {

    private static final String TAG = Factory.class.getSimpleName();
    // 单例模式
    private static final Factory instance;
    // 全局的线程池
    private final Executor executor;
    // 全局的Gson
    private final Gson gson;


    /**
     * 老汉单例
     */
    static {
        instance = new Factory();
    }

    private Factory() {
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
        // 全局的Gson
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS") // 设置时间格式
                // 设置一个过滤器，数据库级别的Model不进行Json转换
                //.setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * Factory中的初始化
     */
    public static void setup() {
        // 初始化XUI
        //XUI.init(Application.getInstance()); //初始化UI框架
        //XUI.debug(true);  //开启UI框架调试日志

        // 初始化数据库
        // 打开数据库
        // 初始化数据库
        FlowManager.init(new FlowConfig.Builder(Application.getInstance())
                .openDatabasesOnInit(true) // 数据库初始化的时候就打开
                .build());

        // 持久化的数据进行初始化
        // 再次打开App时，从本地数据库读取当前账户的User信息，并将有关信息存储到Account中
        Account.load(Application.getInstance());
    }


    /**
     * 异步运行的方法
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable) {
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的Gson，在这里可以对Gson进行一些全局的初始化
     * @return
     */
    public static Gson getGson() {
        return instance.gson;
    }


}
