package com.ysq.wifisignin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.data.HeaderSetting;
import com.ysq.wifisignin.ui.activity.group.GroupCreateActivity;
import com.ysq.wifisignin.ui.activity.group.GroupSearchActivity;
import com.ysq.wifisignin.ui.activity.initiate.AttendActivity;
import com.ysq.wifisignin.ui.activity.user.UpdateInfoActivity;
import com.ysq.wifisignin.ui.common.NavHelper;
import com.ysq.wifisignin.ui.common.WifiBssidActivity;
import com.ysq.wifisignin.ui.frag.initiate.InitiateFragment;
import com.ysq.wifisignin.ui.frag.main.MyGroupFragment;
import com.ysq.wifisignin.ui.frag.main.MyInitiateFragment;
import com.ysq.wifisignin.ui.frag.main.PersonalCenterFragment;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends WifiBssidActivity
        implements NavHelper.OnTabChangedListener<String>,
        BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final String PREF_NAME = "data";
    public static final String PREF_KEY_BING_PIC = "bing_daily_pic";

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

    private DataListenerAdmin.ChangedListener<Object> mHeaderListener;
    private DataListenerAdmin.ChangedListener<Object> mPortraitListener;

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

        DataListenerAdmin.addChangedListener(HeaderSettingActivity.class,
                mHeaderListener = new DataListenerAdmin.ChangedListener<Object>() {
                    @Override
                    public void onDataChanged(int action, Object... dataList) {
                        loadHeader();
                    }
                });
        DataListenerAdmin.addChangedListener(UpdateInfoActivity.class,
                mPortraitListener = new DataListenerAdmin.ChangedListener<Object>() {
                    @Override
                    public void onDataChanged(int action, Object... dataList) {
                        loadPortrait();
                    }
                });

        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_group, new NavHelper.Tab<>(MyGroupFragment.class, "我的群组"))
                .add(R.id.action_initiate, new NavHelper.Tab<>(MyInitiateFragment.class, "我发起的签到"))
                .add(R.id.action_personal, new NavHelper.Tab<>(PersonalCenterFragment.class, "个人中心"));

        mNavigation.setOnNavigationItemSelectedListener(this);

        mFloatAction.setOnClickListener(this);
        mStubIcon.setOnClickListener(this);

        loadHeader();
    }

    @Override
    protected void initData() {
        super.initData();

        // 进入主界面，默认选中我的群组
        Menu menu = mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_group, 0);

        // 加载头像
        loadPortrait();

        // 右上角的图标
        //mStubIcon.setImageResource(R.drawable.ic_search);
    }

    // 当Tab切换后的回调，基类中已经替我们完成了Fragment的切换


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataListenerAdmin.removeChangedListener(HeaderSettingActivity.class, mHeaderListener);
        DataListenerAdmin.removeChangedListener(UpdateInfoActivity.class, mPortraitListener);
    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_action: {
                NavHelper.Tab<String> currentTab = mNavHelper.getCurrentTab();
                if (currentTab.clx == MyGroupFragment.class) {
                    GroupCreateActivity.show(this);
                } else if (currentTab.clx == MyInitiateFragment.class) {
                    tryGetWifiMac();
                }
                break;
            }
            case R.id.img_stub: {
                NavHelper.Tab<String> currentTab = mNavHelper.getCurrentTab();
                if (currentTab.clx == MyGroupFragment.class) {
                    GroupSearchActivity.show(this);
                } else if (currentTab.clx == MyInitiateFragment.class) {
                    //UiHelper.showToast("现在我需要出席的签到");
                    AttendActivity.show(this);
                }
                break;
            }
        }
    }

    @Override
    protected void onGetMacSucceeded(String bssid) {
        // 成功获取当前Wifi的bssid之后，才打开选择群组的fragment
        new InitiateFragment(bssid)
                .show(getSupportFragmentManager(), InitiateFragment.class.getName());
    }

    private void loadPortrait() {
        // 加载头像
        Glide.with(this)
                .load(Account.getSelf().getPhoto())
                .placeholder(R.drawable.passerby)
                .centerCrop()
                .into(mPortrait);
    }

    private void loadHeader() {
        int option = HeaderSetting.getOption();
        if (option == 0) {
            glideHeaderLay(null); // 默认
        } else {
            glideHeaderLay(HeaderSetting.getHeaderUrl());
        }
    }

    private void glideHeaderLay(String url) {
        if (url != null) {
            Glide.with(MainActivity.this)
                    .load(url)
                    .placeholder(R.drawable.bg_src_morning)
                    .centerCrop()
                    .into(new CustomViewTarget<View, Drawable>(mAppbarLay) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            this.view.setBackground(resource);
                        }

                        @Override
                        protected void onResourceCleared(@Nullable Drawable placeholder) {

                        }
                    });
        } else {
            Glide.with(MainActivity.this)
                    .load(R.drawable.bg_src_morning)
                    .centerCrop()
                    .into(new CustomViewTarget<View, Drawable>(mAppbarLay) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            this.view.setBackground(resource);
                        }

                        @Override
                        protected void onResourceCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

    }

}