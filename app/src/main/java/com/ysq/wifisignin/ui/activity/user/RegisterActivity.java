package com.ysq.wifisignin.ui.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.account.RegisterModel;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.MainActivity;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.MsgVerifyView;
import com.ysq.wifisignin.ui.frag.PrivacyFragment;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author passerbyYSQ
 * @create 2020-05-06 23:17
 */
public class RegisterActivity extends MsgVerifyView {

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.img_clear)
    ImageView mClear;
    @BindView(R.id.edit_verify)
    EditText mCode;
    @BindView(R.id.btn_get_code)
    Button mGetCode;
    @BindView(R.id.edit_username)
    EditText mUsername;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.edit_password_confirm)
    EditText mConfirm;
    @BindView(R.id.chk_agree)
    CheckBox mAgree;
    @BindView(R.id.btn_register)
    Button mRegister;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.txt_privacy)
    TextView mPrivacy;

    private RegisterModel model;

    //倒计时60秒,这里不直接写60000,而用1000*60是因为后者看起来更直观,每走一步是1000毫秒也就是1秒
    private CountDownTimer timer = new CountDownTimer(1000 * 60, 1000) {
        @Override
        public void onTick(long l) {
            //mGetCode.setClickable(false);
            mGetCode.setText((l / 1000) + "s");
        }

        @Override
        public void onFinish() {
            mGetCode.setClickable(true);
            mGetCode.setText("获取验证码");
        }
    };

    public static void show(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 监听手机输入框的输入变化，只有正确的手机号回激活“获取验证码”的按钮
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
                    // 手机号不为空，显示clear图标
                    mClear.setVisibility(View.VISIBLE);

                    // 当手机格式正确，允许点击“获取验证码”
                    if (content.matches(Constant.REGEX_PHONE)) {
                        mGetCode.setEnabled(true);
                    } else {
                        mGetCode.setEnabled(false);
                    }
                } else {
                    mClear.setVisibility(View.GONE);
                }
            }
        });
        mAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mRegister.setEnabled(b);
            }
        });
        mGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 禁用“获取验证码”按钮
                mGetCode.setClickable(false);
                // 开始倒计时
                timer.start();

                // 获取验证码
                getCode(mPhone.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrivacyFragment.show(getSupportFragmentManager());
    }

    @OnClick(R.id.img_return)
    void onReturnClick() {
        LoginActivity.show(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        onReturnClick();
    }

    @OnClick(R.id.img_clear)
    void onClearClick() {
        mPhone.setText("");
    }

    @OnClick(R.id.txt_privacy)
    void onPrivacyClick() {
        PrivacyFragment.show(getSupportFragmentManager());
    }

    public CheckBox getmAgree() {
        return mAgree;
    }

    @OnClick(R.id.btn_register)
    void onRegisterClick() {
        String code = mCode.getText().toString().trim();
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirm = mConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            UiHelper.showToast("用户名不能为空");
        } else if (!password.equals(confirm)) {
            UiHelper.showToast("两次输入的密码不一致");
        } else if (TextUtils.isEmpty(password)) {
            UiHelper.showToast("密码不能为空");
        } else if (TextUtils.isEmpty(code)){
            UiHelper.showToast("验证码不能空");
        } else {
            // 显示Loading
            mRegister.setText("");
            mLoading.start();

            // 封装成Model
            model = new RegisterModel(this.getPhone(), username, password);

            // 验证 验证码是否正确
            verifyCode(code);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 防止倒计时还没结束就离开页面
        timer.cancel();
    }

    // 已经在TextChangedListener中检验过了
    // 这里直接返回true即可
    @Override
    protected boolean checkPhoneNum(String phone) {
        if (!TextUtils.isEmpty(phone) &&
               phone.matches(Constant.REGEX_PHONE)) {
            return true;
        } else {
            UiHelper.showToast("请输入正确的手机号");
            return false;
        }
    }

    @Override
    protected void onSendCodeSucceeded() {
        UiHelper.showToast("已成功发送验证码");
    }

    @Override
    protected void onSendCodeFailed() {
        UiHelper.showToast("发送验证码失败");
    }

    @Override
    protected void onVerifySucceeded() {
        // 验证成功，执行注册逻辑
        if (model != null) {
            registerRequest(model);
        }
    }

    @Override
    protected void onVerifyFailed() {
        mLoading.stop();
        mRegister.setText("注册");
        UiHelper.showToast("验证码错误");
    }

    private void registerRequest(RegisterModel model) {
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
                            // 停止Loading，并跳转到主页
                            mLoading.stop();
                            MainActivity.show(RegisterActivity.this);

                            // 不要忘了销毁当前注册页面
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoading.stop();
                            mRegister.setText("注册");
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
                        mRegister.setText("注册");
                        UiHelper.showToast("网络错误");
                    }
                });

            }
        });
    }
}
