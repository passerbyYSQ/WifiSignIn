package com.ysq.wifisignin.net;

import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.LoginModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.api.group.CreateGroupModel;
import com.ysq.wifisignin.bean.api.group.JoinGroupModel;
import com.ysq.wifisignin.bean.api.group.UpdateGroupModel;
import com.ysq.wifisignin.bean.api.sign.InitiateModel;
import com.ysq.wifisignin.bean.api.sign.SignInModel;
import com.ysq.wifisignin.bean.api.user.UpdateUserInfoModel;
import com.ysq.wifisignin.bean.db.Attend;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.GroupMember;
import com.ysq.wifisignin.bean.db.Initiate;
import com.ysq.wifisignin.bean.db.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    // 创建群
    @POST("group/create")
    Call<ResponseModel<Group>> createGroup(@Body CreateGroupModel model);

    // 创建群
    @GET("group/list")
    Call<ResponseModel<List<Group>>> getJoinedGroup();

    // 搜索群
    @GET("group/search/{groupName}")
    Call<ResponseModel<List<Group>>> searchGroup(@Path(value = "groupName") String groupName);

    // 加群
    @POST("group/join")
    Call<ResponseModel<GroupMember>> joinGroup(@Body JoinGroupModel model);

    // 查询群成员
    @GET("group/members/{groupId}")
    Call<ResponseModel<List<GroupMember>>> getAllMember(@Path("groupId") Integer groupId);

    // 修改群资料
    @POST("group/update")
    Call<ResponseModel<Group>> updateGroup(@Body UpdateGroupModel model);

    // 获取我具有管理权限的群
    @GET("group/list/admin")
    Call<ResponseModel<List<Group>>> getAdminGroup();

    // 发起一次签到
    @POST("sign/initiate")
    Call<ResponseModel<Initiate>> initiate(@Body InitiateModel model);

    // 查看我发起的所有的签到
    @GET("sign/initiate/all")
    Call<ResponseModel<List<Initiate>>> getAllMyInitiate();

    // 查看某次签到的出席情况
    @GET("sign/detail/{initiateId}")
    Call<ResponseModel<List<Attend>>> getAttendDetail(@Path("initiateId") Integer initiateId);

    // 获取必应的每日一图
    @GET("http://guolin.tech/api/bing_pic")
    Call<ResponseBody> getBingPic();

    // 此时我需要出席的签到
    @GET("sign/initiate/need")
    Call<ResponseModel<List<Initiate>>> getMyNeedInitiate();

    // 出席
    @POST("sign/attend")
    Call<ResponseModel<Attend>> goAttend(@Body SignInModel model);

    // 我所有的出席记录
    @GET("sign/all")
    Call<ResponseModel<List<Attend>>> getMyAttendHistory();
}
