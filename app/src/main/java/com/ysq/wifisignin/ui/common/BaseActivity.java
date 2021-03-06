package com.ysq.wifisignin.ui.common;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;

import butterknife.ButterKnife;

/**
 * 自己封装的基类Activity
 * @author passerbyYSQ
 * @create 2020-03-05 20:49
 */
public abstract class BaseActivity extends AppCompatActivity {

    private BaseCircleDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 只要是继承BaseActivity的子类，在其创建时就将其引用存到ActivityCollector的集合中
        ActivityCollector.add(this);
        // 锁定竖屏。如果子类中需要横屏，直接复写重新设置即可
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (initArgs(getIntent().getExtras())) {
            // 得到界面id，并设置到Activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            // butterknife的视图绑定必须在setContentView
            initWidget();
            initData(); // 先初始化控件，再初始化界面数据显示
        } else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 只要是继承BaseActivity的子类，在其销毁时就将其引用从ActivityCollector的集合中移除
        ActivityCollector.remove(this);
    }

    /**
     * 初始化窗口
     * @param bundle
     * @return          如果参数正确，返回true；否则返回false
     */
    protected boolean initArgs(Bundle bundle) {

        return true;

    }

    /**
     * 获取当前界面的资源文件id。必须由子类实现
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showLoading() {
        progressDialog = new CircleDialog.Builder()
                .setProgressStyle(ProgressParams.STYLE_SPINNER)//STYLE_HORIZONTAL 或 STYLE_SPINNER
                .setProgressText("Loading...")
                .setCancelable(false)
                //.setProgressColor(CircleColor.FOOTER_BUTTON_TEXT_POSITIVE)
                .setWidth((float) 0.6)
                .show(getSupportFragmentManager());
    }


    public void dismissLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
