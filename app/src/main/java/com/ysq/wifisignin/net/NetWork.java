package com.ysq.wifisignin.net;

import android.text.TextUtils;

import com.ysq.wifisignin.Factory;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络请求的封装
 * 使用retrofit2框架
 */
public class NetWork {

    private static NetWork instance;
    private Retrofit retrofit;
    private OkHttpClient client;

    // 静态代码块。在类初始化时，就获取一个NetWork的实例
    static {
        instance = new NetWork();
    }

    // 私有构造
    private NetWork() {
    }

    public static OkHttpClient getClient() {
        if (instance.client != null) {
            return instance.client;
        }
        // 得到一个OkHttpClient
        instance.client = new OkHttpClient.Builder()
                // 给所有的请求添加一个拦截器，拦截下来，注入token，将请求重定向
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // 拿到我们的请求
                        Request original = chain.request();
                        // 重新进行build
                        Request.Builder builder = original.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken())) {
                            // 注入一个token
                            builder.addHeader("token", Account.getToken());
                        }
                        // 非必须，因为Retrofit内部已经帮我们做了这件事
                        builder.addHeader("Content-Type", "application/json");
                        Request newRequest = builder.build();
                        // 返回
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        return instance.client;
    }

    public static Retrofit getRetrofit() {

        if (instance.retrofit != null) {
            return instance.retrofit;
        }

        // 得到一个OkHttpClient
        OkHttpClient client = getClient();

        Retrofit.Builder builder = new Retrofit.Builder();

        instance.retrofit = builder.baseUrl(Constant.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson())) //设置Json解析器
                .build();

        return instance.retrofit;
    }

    /**
     * 返回一个请求代理
     * @return  RemoteService
     */
    public static RemoteService remote() {
        return NetWork.getRetrofit()
                .create(RemoteService.class);
    }

    // 不进行Json解析
    public static RemoteServiceNotJson remoteNotJson() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://guolin.tech/api/")
                //设置ScalarsConverter
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(RemoteServiceNotJson.class);
    }

}
