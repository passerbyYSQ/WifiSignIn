package com.ysq.wifisignin.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mylhyl.circledialog.CircleDialog;

/**
 * @author passerbyYSQ
 * @create 2020-05-20 20:12
 */
public abstract class WifiBssidActivity extends BaseActivity {

    // 弹出确认提示框，请求用户手动开启定位服务
    protected void showLocationAlertDialog() {
        new CircleDialog.Builder()
                .setTitle("启动服务")
                .setText("APP需要您手动开启定位服务，是否继续？")//内容
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 668);
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
    protected abstract void onGetMacSucceeded(String bssid);
        // 成功获取当前Wifi的bssid之后，才打开选择群组的fragment
//        new InitiateFragment(bssid)
//                .show(getSupportFragmentManager(), InitiateFragment.class.getName());

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
            case 668:
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
