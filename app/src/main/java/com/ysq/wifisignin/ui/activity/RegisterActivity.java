package com.ysq.wifisignin.ui.activity;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mPhone;
    private EditText mUsername;
    private EditText mPassword;
    private Button mRegister;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 判断是否已经登录
        if (Account.isLogin()) {
            MainActivity.show(this);
            finish();
        }

        // 找到控件
        mPhone = findViewById(R.id.edit_phone);
        mUsername = findViewById(R.id.edit_username);
        mPassword = findViewById(R.id.edit_password);
        mRegister = findViewById(R.id.btn_register);

        mRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        RegisterModel model = null;
        if ((model = validate()) != null) {
            showProgressDialog();
            registerRequest(model);
        }
    }

    private RegisterModel validate() {
        // 参数验证
        // 此处简单示例
        String phone = mPhone.getText().toString().trim();
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请正确填写参数！", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new RegisterModel(phone, username, password);
    }

    public void registerRequest(RegisterModel model) {
        RemoteService service = NetWork.remote();
        Call<ResponseModel<User>> call = service.register(model);
        // 网络请求的回调
        call.enqueue(new Callback<ResponseModel<User>>() {
            @Override
            public void onResponse(Call<ResponseModel<User>> call,
                                   Response<ResponseModel<User>> response) {
                final ResponseModel<User> rspModel = response.body();
                if (rspModel.isSucceed()) {
                    User user = rspModel.getResult();
                    // 存到本地数据库
                    user.save();
                    Account.login(user);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            MainActivity.show(RegisterActivity.this);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(RegisterActivity.this, rspModel.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<User>> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(RegisterActivity.this, "网络错误！",
                                Toast.LENGTH_SHORT).show();
                        Log.e("register", t.getStackTrace().toString());
                        Log.e("register", t.getMessage());
                    }
                });

            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
