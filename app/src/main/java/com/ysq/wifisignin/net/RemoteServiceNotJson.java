package com.ysq.wifisignin.net;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author passerbyYSQ
 * @create 2020-05-20 13:01
 */
public interface RemoteServiceNotJson {
    // 获取必应的每日一图
    @GET("bing_pic")
    Call<String> getBingPic();
}
