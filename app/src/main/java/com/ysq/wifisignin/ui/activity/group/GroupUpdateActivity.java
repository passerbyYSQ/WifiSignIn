package com.ysq.wifisignin.ui.activity.group;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.GroupMember;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.net.UploadHelper;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupUpdateActivity extends BaseActivity {
    public static final int CHOOSE_PHOTO = 65;

    @BindView(R.id.img_portrait)
    CircleImageView mGroupPortrait;
    @BindView(R.id.txt_group_name)
    TextView mGroupName;
    @BindView(R.id.txt_enter_password)
    TextView mEnterPassword;
    @BindView(R.id.txt_group_description)
    TextView mDescription;
    @BindView(R.id.txt_group_announcement)
    TextView mAnnouncement;

    @BindView(R.id.lay_enter_password)
    LinearLayout mLayEnterPassword;
    @BindView(R.id.line_under_enter_password)
    View mLineUnderPassword;
    @BindView(R.id.img_arrow_group_name)
    ImageView mArrowGroupName;
    @BindView(R.id.img_arrow_description)
    ImageView mArrowDescription;
    @BindView(R.id.img_arrow_announcement)
    ImageView mArrowAnnouncement;

    @BindView(R.id.img_member_arrow)
    ImageView mMemberArrow;
    @BindView(R.id.lay_all_member)
    FrameLayout mAllMember;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerMember;

    private boolean IS_DISPLAY = false;  // 是否显示群成员列表
    private Group group;

    private GroupMemberAdapter mMemberAdapter;
    private List<GroupMember> members;

    private String portraitUrl;

    // 进入修改页面时，把Group对象传递进来。用于初始化显示数据
    public static void show(Context context, Group group) {
        Intent intent = new Intent(context, GroupUpdateActivity.class);
        intent.putExtra("group", group);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        // 取出数据。如果数据传递失败。返回False，直接结束当前页面
        group = (Group) bundle.getSerializable("group");
        portraitUrl = group.getPhoto();
        //UiHelper.showToast(group.toString());
        return group != null; // 如果数据传递失败。返回False，直接结束当前页面
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

        mRecyclerMember.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerMember.setAdapter(mMemberAdapter = new GroupMemberAdapter());
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化页面数据。group肯定不为空
        // 因为在基类中已经做判断了，传递数据失败会直接finish掉界面

        Glide.with(this)
                .load(group.getPhoto())
                .centerCrop()
                .into(mGroupPortrait);
        mGroupName.setText(group.getGroupName());
        mEnterPassword.setText("您可重置密码");
        mDescription.setText(group.getDescription());
        mAnnouncement.setText(group.getAnnouncement());

        // 非创建者
        if (!Account.getSelf().getUserId().equals(group.getCreatorId())) {
            mLayEnterPassword.setVisibility(View.GONE);
            mLineUnderPassword.setVisibility(View.GONE);
            mArrowGroupName.setVisibility(View.GONE);
            mArrowDescription.setVisibility(View.GONE);
            mArrowAnnouncement.setVisibility(View.GONE);

            // 不可点击
            mGroupName.setClickable(false);
            mDescription.setClickable(false);
            mAnnouncement.setClickable(false);
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_update;
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

    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    // 修改群名
    @OnClick(R.id.txt_group_name)
    void onGroupNameClick() {
        new CircleDialog.Builder()
                .setTitle("群名")
                .setInputText(mGroupName.getText().toString())
                .setInputCounter(16) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        // 更新到界面
                        text = text.trim();
                        if (!TextUtils.isEmpty(text)) {
                            mGroupName.setText(text);
                            return true;
                        }
                        return false;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.txt_enter_password)
    void onEnterPasswordClick() {
        new CircleDialog.Builder()
                .setTitle("加群密码")
                .setInputHint("请输入新的加群密码")
                .setInputCounter(16) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        // 更新到界面
                        text = text.trim();
                        if (!TextUtils.isEmpty(text)) {
                            mEnterPassword.setText(text);
                            return true;
                        }
                        return false;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.txt_group_description)
    void onDescriptionClick() {
        new CircleDialog.Builder()
                .setTitle("群描述")
                .setInputText(mDescription.getText().toString())
                .setInputHeight(125)//输入框高度
                .setInputCounter(350) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        mDescription.setText(text); // 允许为空
                        return true;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.txt_group_announcement)
    void onAnnouncementClick() {
        new CircleDialog.Builder()
                .setTitle("群公告")
                .setInputText(mAnnouncement.getText().toString())
                .setInputHeight(125)//输入框高度
                .setInputCounter(350) // 字数限制
                .setInputShowKeyboard(true)//自动弹出键盘
                .setCancelable(false)
                .setPositiveInput("确定", new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        mAnnouncement.setText(text); // 允许为空
                        return true;
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @OnClick(R.id.lay_all_member)
    void onAllMemberClick() {
        // 旋转图标
        float rotation = 0;

        rotation = (IS_DISPLAY ? 0 : 90);

        mMemberArrow.animate()
                .rotation(rotation)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();

        IS_DISPLAY = !IS_DISPLAY;

        // 判断数据是否已经存在。如果存在直接显示，否则请求服务器
        mRecyclerMember.setVisibility(IS_DISPLAY ? View.VISIBLE : View.INVISIBLE);
        if (members == null){
            requestGetAllMember(group.getGroupId());
        }
    }

    // 网络请求获取群成员
    public void requestGetAllMember(Integer groupId) {
        mLoading.start();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<GroupMember>>> call = service.getAllMember(groupId);
        call.enqueue(new Callback<ResponseModel<List<GroupMember>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<GroupMember>>> call,
                                   Response<ResponseModel<List<GroupMember>>> response) {
                ResponseModel<List<GroupMember>> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.stop();

                        if (rspModel.isSucceed()) {
                            // 回调数据
                            members = rspModel.getResult();
                            mMemberAdapter.replace(members);
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<List<GroupMember>>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.stop();
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }

    // 权限回调
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
                            GroupUpdateActivity.this.portraitUrl = url;
                            //Log.e("阿里oss：", url);
                            // 更新界面
                            Glide.with(GroupUpdateActivity.this)
                                    .load(localPath)
                                    .centerCrop()
                                    .into(mGroupPortrait);
                        } else {
                            UiHelper.showToast("头像上传至阿里OSS失败");
                        }
                    }
                });
            }
        });
    }

    class GroupMemberAdapter extends RecyclerAdapter<GroupMember> {

        @Override
        protected int getItemViewType(int position, GroupMember data) {
            return R.layout.item_joined_group;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<GroupMember> {
            @BindView(R.id.img_group_portrait)
            CircleImageView mMemberPortrait;
            @BindView(R.id.txt_group_name)
            TextView mUserName;
            @BindView(R.id.txt_group_description)
            TextView mOther;
            @BindView(R.id.img_is_admin)
            ImageView mIsAdmin;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(GroupMember data) {
                User user = data.getUser();
                Glide.with(GroupUpdateActivity.this)
                        .load(user.getPhoto())
                        .into(mMemberPortrait);
                String alias = TextUtils.isEmpty(data.getAlias()) ? "" : ("  (备注名:" + data.getAlias() + ")");
                mUserName.setText(user.getUserName() + alias);

                String joinTime = "入群：" + Constant.sdf.format(data.getCreateAt());
                mOther.setText(joinTime);

                if (user.getUserId().equals(group.getCreatorId())) {
                    mIsAdmin.setVisibility(View.VISIBLE);
                    mIsAdmin.setImageResource(R.drawable.ic_creator);
                }
            }
        }
    }

}
