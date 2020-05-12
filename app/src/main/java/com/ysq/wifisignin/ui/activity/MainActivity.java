package com.ysq.wifisignin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.NavHelper;
import com.ysq.wifisignin.ui.frag.main.MyGroupFragment;
import com.ysq.wifisignin.ui.frag.main.MyInitiateFragment;
import com.ysq.wifisignin.ui.frag.main.PersonalCenterFragment;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavHelper.OnTabChangedListener<String>,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.appbar)
    View mAppbarLay;
    @BindView(R.id.img_portrait)
    CircleImageView mPortrait;
    @BindView(R.id.txt_title)
    TextView mTitle;
    @BindView(R.id.img_stub)
    ImageView mStubIcon;
    @BindView(R.id.lay_container)
    FrameLayout mPagerLay;
    @BindView(R.id.btn_action)
    FloatActionButton mFloatAction;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    private NavHelper<String> mNavHelper;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 将顶部状态栏和设置为透明
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_group, new NavHelper.Tab<>(MyGroupFragment.class, "我的群组"))
                .add(R.id.action_initiate, new NavHelper.Tab<>(MyInitiateFragment.class, "我发起的签到"))
                .add(R.id.action_personal, new NavHelper.Tab<>(PersonalCenterFragment.class, "个人中心"));

        mNavigation.setOnNavigationItemSelectedListener(this);

        // 裁剪并加载顶部标题栏的背景
        // 4.11.0新版本已经弃用ViewTarget，GlideDrawable也去掉了
//        Glide.with(this)
//                .load(R.drawable.bg_src_morning)
//                .centerCrop()
//                .into(new CustomViewTarget<View, Drawable>(mAppbarLay) {
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        Log.e("顶部背景onLoadFailed", errorDrawable.toString());
//                    }
//
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        this.view.setBackground(resource);
//                    }
//
//                    @Override
//                    protected void onResourceCleared(@Nullable Drawable placeholder) {
//                        Log.e("顶部背景onLoadFailed", placeholder.toString());
//                    }
//                });

    }

    @Override
    protected void initData() {
        super.initData();

        // 进入主界面，默认选中我的群组
        Menu menu = mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_group, 0);

        // 加载头像
        Glide.with(this)
                .load(Account.getSelf().getPhoto())
                .placeholder(R.drawable.passerby)
                .centerCrop()
                .into(mPortrait);

        // 右上角的图标
        //mStubIcon.setImageResource(R.drawable.ic_search);
    }

    // 当Tab切换后的回调，基类中已经替我们完成了Fragment的切换

    // 当NavigationItem被点击时触发的方法，转接给NavHelper处理
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Log.d("menu" , String.valueOf(item.getItemId()));
        // 对浮动按钮进行隐藏和显示的动画
        float transY = 0;
        float rotation = 0;

        // 切换右上角的图标
        switch (item.getItemId()) {
            case R.id.action_group: {
                mStubIcon.setVisibility(View.VISIBLE);
                mStubIcon.setImageResource(R.drawable.ic_search);

                mFloatAction.setImageResource(R.drawable.ic_create_group);
                rotation = -360;
                break;
            }
            case R.id.action_initiate: {
                mStubIcon.setVisibility(View.VISIBLE);
                mStubIcon.setImageResource(R.drawable.ic_ing);

                mFloatAction.setImageResource(R.drawable.ic_create_initiate);
                rotation = 360;
                break;
            }
            default: {
                mStubIcon.setVisibility(View.GONE);

                // 设置 Y轴位移使悬浮按钮隐藏（被底部导航栏遮挡不可见）
                transY = Ui.dipToPx(getResources(), 76);
            }
        }

        mFloatAction.animate()
                .rotation(rotation) // 设置旋转
                .translationY(transY) // Y轴位移
                .setInterpolator(new AnticipateOvershootInterpolator(1)) // 弹性差值器
                .setDuration(480) // 持续时间
                .start();

        return mNavHelper.performClickMenu(item.getItemId());
    }


    @Override
    public void onTabChanged(NavHelper.Tab<String> newTab, NavHelper.Tab<String> oldTab) {
        // 更新顶部标题
        mTitle.setText(newTab.extra);
    }
}
