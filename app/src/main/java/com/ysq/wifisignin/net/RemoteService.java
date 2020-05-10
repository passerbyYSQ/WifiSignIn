package com.ysq.wifisignin.net;

import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.LoginModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.api.user.UpdateUserInfoModel;
import com.ysq.wifisignin.bean.db.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * 网络请求的所有的接口
 */
public interface RemoteService {
    /**
     * 网络请求的注册接口
     * @param model  RegisterModel
     * @return       ResponseModel<User>
     */
    @POST("account/register")
    Call<ResponseModel<User>> register(@Body RegisterModel model);

    /**
     * 网络请求的登录接口
     * @param model  LoginModel
     * @return       ResponseModel<User>
     */
    @POST("account/login")
    Call<ResponseModel<User>> login(@Body LoginModel model);

    // 退出登录
    @PUT("account/logout")
    Call<ResponseModel<Boolean>> logout();

    // 修改个人信息
    @PUT("user/info")
    Call<ResponseModel<User>> updateUserInfo(@Body UpdateUserInfoModel model);

}
