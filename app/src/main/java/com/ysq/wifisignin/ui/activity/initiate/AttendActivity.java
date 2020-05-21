package com.ysq.wifisignin.ui.activity.initiate;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.mylhyl.circledialog.CircleDialog;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.sign.SignInModel;
import com.ysq.wifisignin.bean.db.Attend;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.Initiate;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.ui.common.WifiBssidActivity;
import com.ysq.wifisignin.util.UiHelper;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 此时我需要出席的签到
 */
public class AttendActivity extends WifiBssidActivity {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private InitiateAdapter mAdapter;
    private SimpleDateFormat endTimeSdf = new SimpleDateFormat(" ~ HH:mm:ss");
    private Integer initiateId;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AttendActivity.class));
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new InitiateAdapter());

        requestMyNeedInitiate();

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMyNeedInitiate();
            }
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_attend;
    }

    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    @Override
    protected void onGetMacSucceeded(String bssid) {
        SignInModel model = new SignInModel(initiateId, bssid);
        requestGoAttend(model);
    }

    class InitiateAdapter extends RecyclerAdapter<Initiate> {

        @Override
        protected int getItemViewType(int position, Initiate data) {
            return R.layout.item_need_attend_initiate;
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
            @BindView(R.id.txt_group_creator)
            TextView mGroupCreator;
            @BindView(R.id.txt_initiate_duration)
            TextView mDuration;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Initiate data) {
                Group group = data.getGroup();
                Glide.with(AttendActivity.this)
                        .load(group.getPhoto())
                        .into(mGroupPortrait);
                mGroupName.setText(group.getGroupName());
                mGroupCreator.setText(data.getUser().getUserName());

                String duration = Constant.sdf.format(data.getStartTime()) +
                        endTimeSdf.format(data.getEndTime());
                mDuration.setText(duration);
            }

            @OnClick(R.id.img_go_attend)
            void onGoAttendClick() {
                new CircleDialog.Builder()
                        .setTitle("确认出席")
                        .setText("如果您所连Wifi跟发起人不一致，会导致出席失败喔！")//内容
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initiateId = mData.getId();
                                tryGetWifiMac();
                            }
                        })
                        .setNegative("取消", null)
                        .show(getSupportFragmentManager());
            }
        }
    }

    // 获取此时我需要出席的签到
    public void requestMyNeedInitiate() {
        mSwipeRefresh.setRefreshing(true);

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Initiate>>> call = service.getMyNeedInitiate();
        call.enqueue(new Callback<ResponseModel<List<Initiate>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Initiate>>> call,
                                   Response<ResponseModel<List<Initiate>>> response) {
                ResponseModel<List<Initiate>> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(false);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(false);
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }

    // 出席
    public void requestGoAttend(SignInModel model) {
        showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<Attend>> call = service.goAttend(model);
        call.enqueue(new Callback<ResponseModel<Attend>>() {
            @Override
            public void onResponse(Call<ResponseModel<Attend>> call,
                                   Response<ResponseModel<Attend>> response) {
                ResponseModel<Attend> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        if (rspModel.isSucceed()) {
                            Attend attend = rspModel.getResult();
                            if (attend != null) {
                                UiHelper.showToast("出席成功");
                                // 刷新一次界面
                                requestMyNeedInitiate();
                            }
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<Attend>> call, Throwable t) {
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
