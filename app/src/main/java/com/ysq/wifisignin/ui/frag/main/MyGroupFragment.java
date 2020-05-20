package com.ysq.wifisignin.ui.frag.main;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.data.Account;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.group.GroupCreateActivity;
import com.ysq.wifisignin.ui.activity.group.GroupUpdateActivity;
import com.ysq.wifisignin.ui.common.BaseFragment;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.ui.frag.group.GroupSearchFragment;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主界面中，我所加入的群组
 */
public class MyGroupFragment extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefresh;

    private RecyclerAdapter<Group> mAdapter;
    private DataListenerAdmin.ChangedListener<Group> mCreateListener;
    private DataListenerAdmin.ChangedListener<Group> mJoinListener;

    private DataListenerAdmin.ChangedListener<Group> mUpdateListener;

    public MyGroupFragment() {
        // Required empty public constructor

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 监听新建群组的页面，当创建成功，将新群组的数据回到过来
        DataListenerAdmin.addChangedListener(GroupCreateActivity.class,
                mCreateListener = new DataListenerAdmin.ChangedListener<Group>() {
                    @Override
                    public void onDataChanged(int action, Group... dataList) {
                        mAdapter.addAtHead(dataList);
                    }
                });
        // 监听GroupSearchFragment，一旦加群成功，会将新加入的群的数据回调过来
        DataListenerAdmin.addChangedListener(GroupSearchFragment.class,
                mJoinListener = new DataListenerAdmin.ChangedListener<Group>() {
                    @Override
                    public void onDataChanged(int action, Group... dataList) {
                        mAdapter.addAtHead(dataList);
                    }
                });

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new JoinedGroupAdapter(
                new RecyclerAdapter.AdapterListenerImpl<Group>() {  // 设置点击事件
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Group data) {
                        GroupUpdateActivity.show(getContext(), data);
                        // 注册监听
                        if (mUpdateListener != null) {
                            DataListenerAdmin.removeChangedListener(
                                    GroupUpdateActivity.class, mUpdateListener);
                        }
                        DataListenerAdmin.addChangedListener(GroupUpdateActivity.class,
                                mUpdateListener = new DataListenerAdmin.ChangedListener<Group>() {
                                    @Override
                                    public void onDataChanged(int action, Group... dataList) {
                                        holder.updateData(dataList[0]);
                                    }
                                });
                    }
                }));
        // 注意一定要早mAdapter赋值之后
        // 在头部插入数据后，自动滚动至头部
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                // 判断是否是在头部插入数据
                if (positionStart == 0) {
                    mRecycler.scrollToPosition(positionStart + itemCount - 1);
                }
            }
        });

        // 注意一定要早mAdapter赋值之后
        requestJoinedGroup();
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestJoinedGroup();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除监听
        DataListenerAdmin.removeChangedListener(GroupCreateActivity.class, mCreateListener);
        DataListenerAdmin.removeChangedListener(GroupSearchFragment.class, mJoinListener);
        DataListenerAdmin.removeChangedListener(GroupUpdateActivity.class, mUpdateListener);
    }

    class JoinedGroupAdapter extends RecyclerAdapter<Group> {
        public JoinedGroupAdapter(AdapterListener<Group> listener) {
            super(listener);
        }

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
            ImageView mIsAdmin;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Group data) {
                Glide.with(MyGroupFragment.this)
                        .load(data.getPhoto())
                        .into(mGroupPortrait);
                mGroupName.setText(data.getGroupName());
                mDescription.setText(data.getDescription());

                if (Account.getSelf().getUserId().equals(data.getCreatorId())) {
                    mIsAdmin.setImageResource(R.drawable.ic_creator);
                    mIsAdmin.setVisibility(View.VISIBLE);
                } else {
                    // 不加这个else，刷新后，原本我不是创建者的群也会出现图标！
                    mIsAdmin.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void requestJoinedGroup() {
        mRefresh.setRefreshing(true);

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Group>>> call = service.getJoinedGroup();
        call.enqueue(new Callback<ResponseModel<List<Group>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Group>>> call,
                                   Response<ResponseModel<List<Group>>> response) {
                ResponseModel<List<Group>> rspModel = response.body();
                // 强制回到主线程
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mRefresh.setRefreshing(false);
                        if (rspModel.isSucceed()) {
                            mAdapter.replace(rspModel.getResult());
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<List<Group>>> call, Throwable t) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mRefresh.setRefreshing(false);
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });

    }

}
