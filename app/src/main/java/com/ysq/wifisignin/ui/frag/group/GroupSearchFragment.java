package com.ysq.wifisignin.ui.frag.group;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.ui.activity.group.GroupSearchActivity;
import com.ysq.wifisignin.ui.common.BaseFragment;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

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
        if (mGroupListener != null) {
            DataListenerAdmin.removeChangedListener(GroupSearchActivity.class, mGroupListener);
        }
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
                UiHelper.showToast("申请加入");
            }
        }
    }

}
