package com.ysq.wifisignin.ui.activity.user;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.LoginModel;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.MainActivity;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.img_clear)
    ImageView mClear;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.txt_to_register)
    TextView mToRegister;
    @BindView(R.id.btn_login)
    Button mLogin;
    @BindView(R.id.loading)
    Loading mLoading;

    public static void show(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                if (!TextUtils.isEmpty(content)) {
                    mClear.setVisibility(View.VISIBLE);
                } else {
                    mClear.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick(R.id.btn_login)
    void onLoginClick() {
        String phone = mPhone.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (!phone.matches(Constant.REGEX_PHONE)) {
            UiHelper.showToast("请输入正确的手机号");
        } else if (TextUtils.isEmpty(password)) {
            UiHelper.showToast("请输入密码");
        } else {
            mLogin.setText("");
            mLoading.start();

            loginRequest(new LoginModel(phone, password));
        }
    }

    @OnClick(R.id.img_clear)
    void onClearClick() {
        mPhone.setText("");
    }

    @OnClick(R.id.txt_to_register)
    void onToRegisterClick() {
        RegisterActivity.show(this);
        finish();
    }

    private void loginRequest(LoginModel model) {
        RemoteService service = NetWork.remote();
        Call<ResponseModel<User>> call = service.login(model);
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
                            // 停止Loading，并跳转到主页
                            mLoading.stop();
                            MainActivity.show(LoginActivity.this);

                            // 不要忘了销毁当前注册页面
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoading.stop();
                            mLogin.setText("登录");
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<User>> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.stop();
                        mLogin.setText("登录");
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }
}
