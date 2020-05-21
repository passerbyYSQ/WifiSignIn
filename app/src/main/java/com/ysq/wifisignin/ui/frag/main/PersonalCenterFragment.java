package com.ysq.wifisignin.ui.frag.main;


import android.view.View;

import com.mylhyl.circledialog.CircleDialog;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.ui.activity.HeaderSettingActivity;
import com.ysq.wifisignin.ui.activity.user.LoginActivity;
import com.ysq.wifisignin.ui.common.ActivityCollector;
import com.ysq.wifisignin.ui.common.BaseFragment;

import butterknife.OnClick;

/**
 * 主界面，个人中心
 */
public class PersonalCenterFragment extends BaseFragment {


    public PersonalCenterFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_personal_center;
    }

    @OnClick(R.id.lay_header_setting)
    void onHeaderSettingClick() {
        HeaderSettingActivity.show(getContext());
    }

    @OnClick(R.id.lay_logout)
    void onLogoutClick() {
        new CircleDialog.Builder()
                .setTitle("退出登录")
                .setText("将会清除该账户此次登录的相关信息，是否继续？")//内容
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Account.logout();
                        ActivityCollector.finishAll();
                        LoginActivity.show(Application.getInstance());
                    }
                })
                .setNegative("取消", null)
                .show(getFragmentManager());
    }

}
