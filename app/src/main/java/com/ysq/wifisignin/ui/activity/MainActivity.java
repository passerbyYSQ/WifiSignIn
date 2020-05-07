package com.ysq.wifisignin.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.user.LoginActivity;
import com.ysq.wifisignin.ui.common.ActivityCollector;
import com.ysq.wifisignin.ui.common.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    @BindView(R.id.txt_self_info)
    TextView mSelf;
    @BindView(R.id.btn_logout)
    Button mLogout;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSelf = findViewById(R.id.txt_self_info);
        mSelf.setText("token：" + Account.getToken());
    }

    @OnClick(R.id.btn_logout)
    void onLogoutClick() {
        RemoteService service = NetWork.remote();
        Call<ResponseModel<Boolean>> call = service.logout();
        call.enqueue(new Callback<ResponseModel<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseModel<Boolean>> call,
                                   Response<ResponseModel<Boolean>> response) {
                ResponseModel<Boolean> model = response.body();
                if (model.isSucceed()) {
                    Account.logout();
                    ActivityCollector.finishAll();
                    LoginActivity.show(Application.getInstance());
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("退出失败，服务端错误");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<Boolean>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("网络错误");
                    }
                });
            }
        });

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert     *file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public String getSignMd5Str() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
