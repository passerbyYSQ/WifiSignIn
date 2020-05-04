package com.ysq.wifisignin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ysq.wifisignin.ui.activity.MainActivity;
import com.ysq.wifisignin.ui.common.BaseActivity;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * App的启动页面。一旦打开App，就会首先进入启动页面
 *
 */
public class LaunchActivity extends BaseActivity {

    // 注意属性不能为private，否则butterknife无法注入
    @BindView(R.id.img_background)
    ImageView mBackground;

    @BindView(R.id.loading)
    Loading mLoading;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            MainActivity.show(LaunchActivity.this);
            finish();
            return true;
        }
    });

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 将顶部状态栏和底部导航栏设置透明
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        // 加载背景图
        Glide.with(this)
                .load(R.drawable.bg_moon)
                .centerCrop()
                .into(mBackground);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.start);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoading.setProgress(1);
                mLoading.stop();
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBackground.startAnimation(animation);
    }
}
