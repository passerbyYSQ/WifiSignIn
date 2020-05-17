package com.ysq.wifisignin.ui.frag.group;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.group.JoinGroupModel;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.GroupMember;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.group.GroupSearchActivity;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.BaseFragment;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用于显示搜索群的结果
 */
public class GroupSearchFragment extends BaseFragment {

    @BindView(R.id.recycler_group_search)
    RecyclerView mRecyclerGroupSearch;

    private GroupAdapter mGroupAdapter;
    private DataListenerAdmin.ChangedListener<Group> mGroupListener;

    public GroupSearchFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group_search;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化RecyclerView相关
        mRecyclerGroupSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerGroupSearch.setAdapter(mGroupAdapter = new GroupAdapter());

        // 注册监听器。一定要在mGroupAdapter赋值之后
        DataListenerAdmin.addChangedListener(GroupSearchActivity.class,
                mGroupListener = new DataListenerAdmin.ChangedListener<Group>() {
                    @Override
                    public void onDataChanged(int action, Group... dataList) {
                        mGroupAdapter.replace(dataList);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除监听
        DataListenerAdmin.removeChangedListener(GroupSearchActivity.class, mGroupListener);
    }

    class GroupAdapter extends RecyclerAdapter<Group> {

        @Override
        protected int getItemViewType(int position, Group data) {
            return R.layout.item_joined_group;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {
            @BindView(R.id.img_group_portrait)
            CircleImageView mGroupPortrait;
            @BindView(R.id.txt_group_name)
            TextView mGroupName;
            @BindView(R.id.txt_group_description)
            TextView mDescription;
            @BindView(R.id.img_is_admin)
            ImageView mJoinIcon;  // 申请加入的图标

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Group data) {
                Glide.with(GroupSearchFragment.this)
                        .load(data.getPhoto())
                        .into(mGroupPortrait);
                mGroupName.setText(data.getGroupName());
                mDescription.setText(data.getDescription());

                // 将占位图标替换为 申请加入的图标
                mJoinIcon.setImageResource(R.drawable.sel_opt_done_add);
                // 设置背景颜色
                mJoinIcon.setBackground(getResources().getDrawable(R.drawable.sel_btn_bg_accent_4));
            }

            // 点击申请加入的图标
            @OnClick(R.id.img_is_admin)
            void onApplyJoinClick() {
                //UiHelper.showToast("申请加入");
                new CircleDialog.Builder()
                        .setTitle("加群")
                        .setInputHint("请输入正确的加群密码")
                        .setInputCounter(32) // 字数限制
                        .setInputShowKeyboard(true)//自动弹出键盘
                        .setCancelable(false)
                        .setPositiveInput("确定", new OnInputClickListener() {
                            @Override
                            public boolean onClick(String text, View v) {
                                String enterPassword = text.trim();
                                if (TextUtils.isEmpty(enterPassword)) {
                                    UiHelper.showToast("加群密码不能为空");
                                    return false;
                                } else {
                                    requestJoinGroup(new JoinGroupModel(mData.getGroupId(),
                                            enterPassword, ""), mData);
                                    return true;
                                }
                            }
                        })
                        .setNegative("取消", null)
                        .show(getChildFragmentManager());
            }
        }
    }

    public void requestJoinGroup(JoinGroupModel model, Group currentGroup) {
        BaseActivity parent = (BaseActivity) getActivity();
        parent.showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<GroupMember>> call = service.joinGroup(model);
        call.enqueue(new Callback<ResponseModel<GroupMember>>() {
            @Override
            public void onResponse(Call<ResponseModel<GroupMember>> call,
                                   Response<ResponseModel<GroupMember>> response) {
                ResponseModel<GroupMember> rspModel = response.body();
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        parent.dismissLoading();
                        if (rspModel.isSucceed()) {
                            UiHelper.showToast("加入成功");
                            DataListenerAdmin.notifyChanged(GroupSearchFragment.class,
                                    DataListenerAdmin.ChangedListener.ACTION_ADD, currentGroup);
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<GroupMember>> call, Throwable t) {
                // 强制回到主线程
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        parent.dismissLoading();
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }
}
