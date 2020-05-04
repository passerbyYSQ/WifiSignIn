package com.ysq.wifisignin.net;

import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.db.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 网络请求的所有的接口
 */
public interface RemoteService {
    /**
     * 网络请求的注册接口
     * @param model  RegisterModel
     * @return       RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<ResponseModel<User>> register(@Body RegisterModel model);


}
