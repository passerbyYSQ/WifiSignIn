package com.ysq.wifisignin.ui.activity.group;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.group.CreateGroupModel;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.net.UploadHelper;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.util.UiHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupCreateActivity extends BaseActivity  {

    @BindView(R.id.img_portrait)
    CircleImageView mPortrait;
    @BindView(R.id.img_camera)
    ImageView mCamera;
    @BindView(R.id.txt_group_name)
    EditText mGroupName;
    @BindView(R.id.txt_enter_password)
    EditText mEnterPassword;
    @BindView(R.id.txt_group_description)
    EditText mDescription;
    @BindView(R.id.txt_my_alias)
    EditText mMyAlias;
    @BindView(R.id.btn_group_create)
    Button mGroupCreate;

    public static final int CHOOSE_PHOTO = 65;
    private String portraitUrl;


    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    @OnClick(R.id.btn_group_create)
    void onGroupCreateClick() {
        String groupName = mGroupName.getText().toString().trim();
        String enterPassword = mEnterPassword.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        String myAlias = mMyAlias.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            UiHelper.showToast("群名称不能为空");
        } else if (TextUtils.isEmpty(enterPassword)) {
            UiHelper.showToast("加群密码不能为空");
        } else if (TextUtils.isEmpty(myAlias)) {
            UiHelper.showToast("在群的备注名不能为空");
        } else if (TextUtils.isEmpty(portraitUrl)) {

            new CircleDialog.Builder()
                    .setTitle("提醒")
                    .setText("你尚未选择头像，是否确认使用默认头像？")//内容
                    .setPositive("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            portraitUrl = Constant.DEFAULT_PORTRAIT_URL;
                            // 封装Model
                            //UiHelper.showToast("创建成功");
                            CreateGroupModel model = new CreateGroupModel(groupName, enterPassword,
                                    portraitUrl, description, myAlias);
                            requestCreateGroup(model);
                        }
                    })
                    .setNegative("取消", null)
                    .show(getSupportFragmentManager());

        } else {
            //UiHelper.showToast("创建成功");
            CreateGroupModel model = new CreateGroupModel(groupName, enterPassword,
                    portraitUrl, description, myAlias);
            requestCreateGroup(model);
        }

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
                .configItems(new ConfigItems() {
                    @Override
                    public void onConfig(ItemsParams params) {
                        params.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    // 打开相册
    public void openAlbum() {
        //UiHelper.showToast("打开相册");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
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
                        Glide.with(this)
                                .load(resultUri)
                                .centerCrop()
                                .into(mPortrait);
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
                            // 将url暂存起来，之后存储到服务器
                            GroupCreateActivity.this.portraitUrl = url;
                            //Log.e("阿里oss：", url);
                            // 更新界面
                            Glide.with(GroupCreateActivity.this)
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

    private void requestCreateGroup(CreateGroupModel model) {
        showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<Group>> call = service.createGroup(model);

        call.enqueue(new Callback<ResponseModel<Group>>() {
            @Override
            public void onResponse(Call<ResponseModel<Group>> call,
                                   Response<ResponseModel<Group>> response) {
                ResponseModel<Group> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        if (rspModel.isSucceed()) {
                            Group group = rspModel.getResult();
                            if (group != null) {
                                UiHelper.showToast("创建成功");
                                finish();
                                // 通知更新
                                DataListenerAdmin.notifyChanged(GroupCreateActivity.class,
                                        DataListenerAdmin.ChangedListener.ACTION_ADD, group);
                            }
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<ResponseModel<Group>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }
}
