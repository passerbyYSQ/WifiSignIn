package com.ysq.wifisignin.ui.activity.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.yalantis.ucrop.UCrop;
import com.ysq.wifisignin.Application;
import com.ysq.wifisignin.Factory;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.user.UpdateUserInfoModel;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.net.UploadHelper;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.util.UiHelper;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 复制回来的时候忘了在manifest.xml中注册了
 */
public class UpdateInfoActivity extends BaseActivity {
    @BindView(R.id.img_portrait)
    CircleImageView mPortrait;
    @BindView(R.id.img_camera)
    ImageView mCamara;
    @BindView(R.id.txt_phone)
    TextView mPhone;
    @BindView(R.id.txt_username)
    TextView mUsername;
    @BindView(R.id.txt_sex)
    TextView mSex;
    @BindView(R.id.txt_description)
    TextView mDescription;
    @BindView(R.id.btn_update)
    Button mUpdate;

    private Integer sex;
    private String portraitUrl;

    public static final int CHOOSE_PHOTO = 65;
    public static final int TAKE_PHOTO = 66;
    private Uri outputImgUri;

    public static void show(Context context) {
        context.startActivity(new Intent(context, UpdateInfoActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_update_info;
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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.btn_update)
    void onUpdateClick() {
        String username = mUsername.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        UpdateUserInfoModel model = new UpdateUserInfoModel(
                username, portraitUrl, sex, description);
        updateUserInfoRequest(model);
    }

    @OnClick(R.id.img_camera)
    void onCameraClick() {
        // 申请相册相关权限
        if (ContextCompat.checkSelfPermission(this,
             Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return;
        }

        final String[] items = new String[]{"相册", "拍照"};
        new CircleDialog.Builder()
                .setItems(items, new OnRvItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        if (position == 0) {
                            // 打开相册
                            openAlbum();
                        } else {
                            // 打开相机
                            openCamera();
                        }
                        return true;
                    }
                })//Arrays或List to ListView
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraClick();
                } else {
                    UiHelper.showToast("您拒绝了授予权限");
                }
                break;
            }
        }
    }

    @OnClick(R.id.txt_username)
    void onUsernameClick() {
        new CircleDialog.Builder()
                .setTitle("用户名")
                .setInputText(mUsername.getText().toString())
                .setInputCounter(16) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        // 更新到界面
                        if (!TextUtils.isEmpty(text)) {
                            mUsername.setText(text);
                            return true;
                        }
                        return false;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.txt_description)
    void onDescriptionClick() {
        new CircleDialog.Builder()
                .setTitle("个人简介")
                .setInputText(mDescription.getText().toString())
                .setInputHeight(125)//输入框高度
                .setInputCounter(200) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        mDescription.setText(text);
                        return true;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.txt_sex)
    void onSexClick() {
        final String[] items = new String[]{"男", "女", "保密"};
        new CircleDialog.Builder()
                .setItems(items, new OnRvItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        switch (position) {
                            case 0: {
                                mSex.setText("男");
                                UpdateInfoActivity.this.sex = User.MALE;
                                break;
                            }
                            case 1: {
                                mSex.setText("女");
                                UpdateInfoActivity.this.sex = User.FEMALE;
                                break;
                            }
                            case 2: {
                                mSex.setText("保密");
                                UpdateInfoActivity.this.sex = User.SECRET;
                                break;
                            }
                        }
                        return true;
                    }
                })//Arrays或List to ListView
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @Override
    protected void initData() {
        super.initData();
        // 进入页面加载数据
        User self = Account.getSelf();

        this.portraitUrl = self.getPhoto();
        Glide.with(this)
                .load(this.portraitUrl)
                .placeholder(R.drawable.passerby)
                .centerCrop()
                .into(mPortrait);

        mPhone.setText(self.getPhone());
        mUsername.setText(self.getUserName());

        this.sex = self.getSex();
        mSex.setText(this.sex ==  User.MALE ? "男" : "女");

        mDescription.setText(self.getDescription());
    }

    // 打开相册
    public void openAlbum() {
        //UiHelper.showToast("打开相册");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    public void openCamera() {
        File outputImg = new File(Application.getCacheDirFile(), "output_image.jpg");
        try {
            if (outputImg.exists()) {
                outputImg.delete();
            }
            outputImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 启动相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= 24) {
            outputImgUri = FileProvider.getUriForFile(this,
                    "com.ysq.wifisignin.fileprovider", outputImg);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            outputImgUri = Uri.fromFile(outputImg);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImgUri);
        startActivityForResult(intent, TAKE_PHOTO);
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
                        uploadPortrait(resultUri.getPath());
//                        Glide.with(this)
//                                .load(resultUri)
//                                .centerCrop()
//                                .into(mPortrait);
                    }
                    break;
                }
                case CHOOSE_PHOTO: {
                    startCrop(data.getData());
                    break;
                }
                case TAKE_PHOTO: {
                    startCrop(outputImgUri);
                    break;
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UiHelper.showToast("未知错误");
        }
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
                            // 将url暂存起来，之后存储到服务器
                            UpdateInfoActivity.this.portraitUrl = url;
                            Log.e("阿里oss：", url);
                            // 更新界面
                            Glide.with(UpdateInfoActivity.this)
                                    .load(localPath)
                                    .centerCrop()
                                    .into(mPortrait);
                        } else {
                            UiHelper.showToast("头像上传至阿里OSS失败");
                        }
                    }
                });
            }
        });
    }

    // 请求修改信息
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateUserInfoRequest(UpdateUserInfoModel model) {
        // 显示Loading
        showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<User>> call = service.updateUserInfo(model);
        call.enqueue(new Callback<ResponseModel<User>>() {
            @Override
            public void onResponse(Call<ResponseModel<User>> call,
                                   Response<ResponseModel<User>> response) {
                ResponseModel<User> rspModel = response.body();
                // 强制回到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 销毁Loading
                        dismissLoading();
                        if (rspModel.isSucceed()) {
                            // 将数据更新到本地数据库
                            User self = rspModel.getResult();
                            self.save();
                            UiHelper.showToast("修改成功");
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<User>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 销毁Loading
                        dismissLoading();
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }
}
