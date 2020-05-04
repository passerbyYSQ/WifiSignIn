package com.ysq.wifisignin.net.account;

import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author passerbyYSQ
 * @create 2020-04-21 22:00
 */
public class AccountRequest {

    public static void registerRequest(RegisterModel model) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        // 请求服务器的接口去注册，并返回一个回调
        Call<ResponseModel<User>> call = service.register(model);

        // 请求完成后的回调函数
        call.enqueue(new Callback<ResponseModel<User>>() {
            @Override
            public void onResponse(Call<ResponseModel<User>> call,
                                   Response<ResponseModel<User>> response) {

            }

            @Override
            public void onFailure(Call<ResponseModel<User>> call, Throwable t) {

            }
        });
    }

}
