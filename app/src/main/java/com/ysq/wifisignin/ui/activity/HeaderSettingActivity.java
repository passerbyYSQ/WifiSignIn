package com.ysq.wifisignin.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.yalantis.ucrop.UCrop;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.Factory;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.data.HeaderSetting;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.net.UploadHelper;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.util.UiHelper;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeaderSettingActivity extends BaseActivity {

    public static final int CHOOSE_PHOTO = 65;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.img_preview)
    ImageView mPreview;

    private int option;
    private Uri customUri;
    private String bingUrl;

    @IdRes
    int mPreRadioId;
    @IdRes
    int mCurRadioId;

    public static void show(Context context) {
        context.startActivity(new Intent(context, HeaderSettingActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_header_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mPreRadioId = mRadioGroup.getCheckedRadioButtonId();
        mCurRadioId = mPreRadioId;

        option = HeaderSetting.getOption();
        switch (option) {
            case HeaderSetting.OPTION_DEFAULT: {
                ((RadioButton) findViewById(R.id.radio_default)).setChecked(true);
                break;
            }
            case HeaderSetting.OPTION_BING: {
                ((RadioButton) findViewById(R.id.radio_bing)).setChecked(true);
                break;
            }
            case HeaderSetting.OPTION_CUSTOM: {
                ((RadioButton) findViewById(R.id.radio_custom)).setChecked(true);
                break;
            }
        }

    }

    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    // 保存设置
    @OnClick(R.id.txt_save)
    void onSaveSettingClick() {
        switch (option) {
            case HeaderSetting.OPTION_DEFAULT: {
                HeaderSetting.save(option, "");
                UiHelper.showToast("保存成功");
                // 通知更新
                DataListenerAdmin.notifyChanged(HeaderSettingActivity.class,
                        DataListenerAdmin.ChangedListener.ACTION_UPDATE);
                break;
            }
            case HeaderSetting.OPTION_BING: {
                HeaderSetting.save(option, bingUrl);
                UiHelper.showToast("保存成功");

                DataListenerAdmin.notifyChanged(HeaderSettingActivity.class,
                        DataListenerAdmin.ChangedListener.ACTION_UPDATE);
                break;
            }
            case HeaderSetting.OPTION_CUSTOM: {
                uploadPortrait(customUri.getPath());
                break;
            }
        }
    }

    // 默认
    @OnClick(R.id.radio_default)
    void onDefaultChecked() {
        option = HeaderSetting.OPTION_DEFAULT;
        mPreRadioId = mCurRadioId;
        mCurRadioId = mRadioGroup.getCheckedRadioButtonId();

        // 防止同一个点击多次，造成加载多次
        if (mCurRadioId != mPreRadioId) {
            Glide.with(this)
                    .load(R.drawable.bg_src_morning)
                    .into(mPreview);
        }
    }

    // bing
    @OnClick(R.id.radio_bing)
    void onBingChecked() {
        option = HeaderSetting.OPTION_BING;
        mPreRadioId = mCurRadioId;
        mCurRadioId = mRadioGroup.getCheckedRadioButtonId();

        // 防止重复请求
        if (bingUrl != null) {
            Glide.with(HeaderSettingActivity.this)
                    .load(bingUrl)
                    .into(mPreview);
        } else {
            requestBingPic();
        }
    }

    // 自定义
    @OnClick(R.id.radio_custom)
    void onCustomRadioChecked() {
        option = HeaderSetting.OPTION_CUSTOM;
        mPreRadioId = mCurRadioId;
        mCurRadioId = mRadioGroup.getCheckedRadioButtonId();

        // 同一个点击多次，代表用户想重新上传另一张图片

        // 申请相册相关权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return;
        }

        final String[] items = new String[]{"相册"};
        new CircleDialog.Builder()
                .setItems(items, new OnRvItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        if (position == 0) {
                            // 打开相册
                            openAlbum();
                        }
                        return true;
                    }
                })//Arrays或List to ListView
                .setCancelable(false)
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
                    }
                })
                .setNegative("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 选中上一个按钮
                        ((RadioButton) findViewById(mPreRadioId)).setChecked(true);
                    }
                })
                .show(getSupportFragmentManager());
    }


    // 打开相册
    public void openAlbum() {
        //UiHelper.showToast("打开相册");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    // 权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCustomRadioChecked();
                } else {
                    UiHelper.showToast("您拒绝了授予权限");
                    // 选中上一个radio
                    ((RadioButton) findViewById(mPreRadioId)).setChecked(true);
                }
                break;
            }
        }
    }

    // 裁剪结果回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 传递过来的回调，然后取出其中的值进行图片加载
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP: {
                    // 通过UCrop得到对应的Uri
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        //uploadPortrait(resultUri.getPath());
                        customUri = resultUri; // 暂存，保存再之后再上传
                        // 先预览输出，之后再上传
                        Glide.with(this)
                                .load(resultUri)
                                .into(mPreview);
                    }
                    break;
                }
                case CHOOSE_PHOTO: {
                    startCrop(data.getData());
                    break;
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UiHelper.showToast("未知错误");
        }
    }

    private void loadPreview(String url) {

    }

    public void startCrop(Uri srcPhoto) {
        UCrop.Options options = new UCrop.Options();
        // 设置图片处理后的格式JPEG
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        // 设置压缩后的图片精度
        options.setCompressionQuality(96);

        // 得到头像的缓存地址
        File dpath = Application.getPortraitTmpFile();

        UCrop.of(srcPhoto, Uri.fromFile(dpath))
                .withAspectRatio(1, 1)  // 1比1比例
                .withMaxResultSize(520, 520) //返回最大的尺寸
                .withOptions(options) // 传递相关参数
                .start(this);
    }

    // 往阿里OSS上传头像
    public void uploadPortrait(String localPath) {
        // 用子线程将本地图片上传到阿里OSS
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = UploadHelper.uploadPortrait(localPath);
                // 如果为空，表示头像上传到阿里OSS失败
                // 回到主线程更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(url)) {
                            HeaderSetting.save(HeaderSetting.OPTION_CUSTOM, url);
                            UiHelper.showToast("保存成功");

                            DataListenerAdmin.notifyChanged(HeaderSettingActivity.class,
                                    DataListenerAdmin.ChangedListener.ACTION_UPDATE);
                        } else {
                            UiHelper.showToast("保存失败：头像上传至阿里OSS失败");
                        }
                    }
                });
            }
        });
    }

    public void requestBingPic() {

        RemoteService service = NetWork.remote();
        Call<ResponseBody> call = service.getBingPic();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String bingPicUrl = new String(response.body().bytes());
                            //glideHeaderLay(bingPicUrl);
                            bingUrl = bingPicUrl;

                            Glide.with(HeaderSettingActivity.this)
                                    .load(bingPicUrl)
                                    .into(mPreview);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        UiHelper.showToast("加载必应图片失败");
                    }
                });
            }
        });

    }
}
