package com.ysq.wifisignin.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mylhyl.circledialog.CircleDialog;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.ui.activity.group.GroupCreateActivity;
import com.ysq.wifisignin.ui.activity.group.GroupSearchActivity;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.NavHelper;
import com.ysq.wifisignin.ui.frag.initiate.InitiateFragment;
import com.ysq.wifisignin.ui.frag.main.MyGroupFragment;
import com.ysq.wifisignin.ui.frag.main.MyInitiateFragment;
import com.ysq.wifisignin.ui.frag.main.PersonalCenterFragment;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavHelper.OnTabChangedListener<String>,
        BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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

        mFloatAction.setOnClickListener(this);
        mStubIcon.setOnClickListener(this);
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
                    UiHelper.showToast("现在我需要出席的签到");
                }
                break;
            }
        }
    }

    // 弹出确认提示框，请求用户手动开启定位服务
    private void showLocationAlertDialog() {
        new CircleDialog.Builder()
                .setTitle("启动服务")
                .setText("APP需要您手动开启定位服务，是否继续？")//内容
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }


    /**
     * 成功获取到当前的Wifi的bssid（mac地址）之后
     * 做的操作，比如说请求存到服务端
     *
     * @param bssid
     */
    public void onGetMacSucceeded(String bssid) {
        // 成功获取当前Wifi的bssid之后，才打开选择群组的fragment
        new InitiateFragment(bssid)
                .show(getSupportFragmentManager(), InitiateFragment.class.getName());
    }

    /**
     * 尝试获取Wifi的Mac地址
     *
     * @return
     */
    public void tryGetWifiMac() {
        if (!isWifiEnabled()) {
            Toast.makeText(this, "请连接Wifi", Toast.LENGTH_SHORT).show();
            return;
        }

        int curSdk = Build.VERSION.SDK_INT;
        String bssid = null;
        if (curSdk >= 27) {
            // 判断是否授予APP定位权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                if (!isLocationServiceEnable()) {
                    // 让用户手动开启定位服务
                    showLocationAlertDialog();
                    return;
                } else {
                    bssid = getWifiMac();
                }
            }
        } else {
            bssid = getWifiMac();
        }

        onGetMacSucceeded(bssid);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tryGetWifiMac();
                } else {
                    Toast.makeText(this, "您拒绝了APP启用定位服务",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default: {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1: {
                tryGetWifiMac();
                break;
            }
            default: {
            }
        }
    }

    /**
     * 判断是否已连接Wifi
     *
     * @return
     */
    public boolean isWifiEnabled() {
        Context appContext = getApplicationContext();
        WifiManager wifiMgr = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) appContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 获取当前Wifi的mac地址
     *
     * @return
     */
    public String getWifiMac() {
        WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        String bssid = wifiManager.getConnectionInfo().getBSSID();
        return "02:00:00:00:00:00".equals(bssid) ? null : bssid;
    }

    /**
     * 判断当前的手机的定位服务是否已开启
     *
     * @return
     */
    public boolean isLocationServiceEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }
}
