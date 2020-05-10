package com.ysq.wifisignin.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.util.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * 上传工具类，用于上传任意文件到阿里OSS存储
 */
public class UploadHelper {

    private static final String TAG = UploadHelper.class.getSimpleName();
    // 与存储区域有关系
    public static final String ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";
    // 上传的仓库名
    private static final String BUCKET_NAME = "italker-ysq"; // 需要改动，我需要在阿里云oss创建一个对应我们app的仓库

    private static OSS getClient() {
        // 新版本OSS建议采用签名Token的方式
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                Constant.ALIYUN_ACCESS_KEY_ID,
                Constant.ALIYUN_ACCESS_KEY_SECRET
        );

        return new OSSClient(Application.getInstance(), ENDPOINT, credentialProvider);
    }

    /**
     * 【同步上传】的最终方法，成功则返回一个公开url
     * 在Android中，只能在子线程调用、而不能在UI线程调用同步接口，否则将出现异常。
     * 如果希望直接在UI线程中上传，请使用异步接口。
     * @param objKey    上传上去后，在服务器上的独立的key
     * @param path      需要上传的文件的路径
     * @return          存储的地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求。
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);
        try {
            // 初始化上传的Client
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult result = client.putObject(request);
            // 得到一个外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            // 格式打印输出的到日志
            //Log.e(TAG, String.format("PublicObjectURL:%s", url));
            return url;

        } catch (Exception e) {
            Log.e(TAG, "【异常】" + Log.getStackTraceString(e));
            // 如果有异常则返回null
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path  本地地址
     * @return      服务器中的地址
     */
    public static String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像
     * @param path  本地地址
     * @return      服务器中的地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        //Log.e(TAG, String.format("objKey:%s", key));
        return upload(key, path);
    }

    /**
     * 上传音频
     * @param path  本地地址
     * @return      服务器中的地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储，避免一个文件夹太多文件
     * @return  yyyyMM
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    // image/202001/abvavbalasck.jpg
    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateString();
        return String.format("image/%s/%s.jpg", dateStr, fileMd5);
    }

    // portrait/202001/abvavbalasck.jpg
    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateString();
        return String.format("portrait/%s/%s.jpg", dateStr, fileMd5);
    }

    // audio/202001/abvavbalasck.mp3
    private static String getAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateString();
        return String.format("audio/%s/%s.mp3", dateStr, fileMd5);
    }
}
