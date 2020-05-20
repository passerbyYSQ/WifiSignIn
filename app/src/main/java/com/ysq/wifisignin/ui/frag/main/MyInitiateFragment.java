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
import com.ysq.wifisignin.bean.db.Initiate;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.activity.initiate.AttendDetailActivity;
import com.ysq.wifisignin.ui.common.BaseFragment;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.ui.frag.initiate.InitiateFragment;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主界面，我发起的签到活动.
 */
public class MyInitiateFragment extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefresh;

    private RecyclerAdapter<Initiate> mAdapter;

    private DataListenerAdmin.ChangedListener<Initiate> mInitiateListener;
    private DataListenerAdmin.ChangedListener<Group> mUpdateListener;
    private SimpleDateFormat endTimeSdf = new SimpleDateFormat(" ~ HH:mm:ss");

    public MyInitiateFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_initiate;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 监听数据
        DataListenerAdmin.addChangedListener(InitiateFragment.class,
                mInitiateListener = new DataListenerAdmin.ChangedListener<Initiate>() {
                    @Override
                    public void onDataChanged(int action, Initiate... dataList) {
                        mAdapter.addAtHead(dataList);
                    }
                });


        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new MyInitiateAdapter());
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Initiate>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Initiate data) {
                AttendDetailActivity.show(getContext(), data.getId());
            }
        });

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

        requestMyAllInitiate();
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMyAllInitiate();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DataListenerAdmin.removeChangedListener(InitiateFragment.class, mInitiateListener);
    }

    class MyInitiateAdapter extends RecyclerAdapter<Initiate> {
        @Override
        protected int getItemViewType(int position, Initiate data) {
            return R.layout.item_my_initiate;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<Initiate> {
            @BindView(R.id.img_group_portrait)
            CircleImageView mGroupPortrait;
            @BindView(R.id.txt_group_name)
            TextView mGroupName;
            @BindView(R.id.txt_initiate_duration)
            TextView mDuration;
            @BindView(R.id.img_is_under_way)
            ImageView mUnderWay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Initiate data) {
                Group group = data.getGroup();
                Glide.with(MyInitiateFragment.this)
                        .load(group.getPhoto()) // 是否有？？？
                        .into(mGroupPortrait);
                mGroupName.setText(group.getGroupName());

                String duration = Constant.sdf.format(data.getStartTime()) +
                        endTimeSdf.format(data.getEndTime());
                mDuration.setText(duration);

                Date current = new Date();
                if (current.after(data.getStartTime())
                        && current.before(data.getEndTime())) {
                    mUnderWay.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void requestMyAllInitiate() {
        mRefresh.setRefreshing(true);

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Initiate>>> call = service.getAllMyInitiate();
        call.enqueue(new Callback<ResponseModel<List<Initiate>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Initiate>>> call,
                                   Response<ResponseModel<List<Initiate>>> response) {
                ResponseModel<List<Initiate>> rspModel = response.body();
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
            public void onFailure(Call<ResponseModel<List<Initiate>>> call, Throwable t) {
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
