package com.ysq.wifisignin.ui.activity.initiate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.ysq.wifisignin.bean.db.Attend;
import com.ysq.wifisignin.bean.db.User;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 某个签到活动的出席情况
 */
public class AttendDetailActivity extends BaseActivity {

//    @BindView(R.id.img_return)
//    ImageView mReturn;
    @BindView(R.id.txt_count_not_attend)
    TextView mCountNotAttend;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private AttendAdapter mAdapter;

    private Integer initiateId;

    public static void show(Context context, Integer initiateId) {
        Intent intent = new Intent(context, AttendDetailActivity.class);
        intent.putExtra("initiateId", initiateId);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new AttendAdapter());

        requestAttendDetail(initiateId);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAttendDetail(initiateId);
            }
        });
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        initiateId = bundle.getInt("initiateId");
        return initiateId != null;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_attend_detail;
    }

    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    private void calCountNotAttend(List<Attend> attends) {
        int count = 0;
        for (Attend attend : attends) {
            if (attend.getStatus().equals(Attend.STATUS_NOT_ATTEND)) {
                count++;
            }
        }
        mCountNotAttend.setText(count + "人未出席");
    }

    class AttendAdapter extends RecyclerAdapter<Attend> {

        @Override
        protected int getItemViewType(int position, Attend data) {
            return R.layout.item_attend_detail;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<Attend> {
            @BindView(R.id.img_user_portrait)
            CircleImageView mPortrait;
            @BindView(R.id.txt_user_name)
            TextView mUsername;
            @BindView(R.id.txt_attend_time)
            TextView mAttendTime;
            @BindView(R.id.img_is_attended)
            ImageView mIsAttend;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Attend data) {
                User user = data.getUser();
                Glide.with(AttendDetailActivity.this)
                        .load(user.getPhoto())
                        .into(mPortrait);
                mUsername.setText(user.getUserName());

                if (data.getStatus().equals(Attend.STATUS_ATTENDED)) {
                    mAttendTime.setText(Constant.sdf.format(data.getTime()));
                    mIsAttend.setImageResource(R.drawable.ic_attended);
                } else {
                    mAttendTime.setText("");
                    mIsAttend.setImageResource(R.drawable.ic_not_attend);
                }
            }
        }
    }

    public void requestAttendDetail(Integer initiateId) {
        mSwipeRefresh.setRefreshing(true);

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Attend>>> call = service.getAttendDetail(initiateId);
        call.enqueue(new Callback<ResponseModel<List<Attend>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Attend>>> call,
                                   Response<ResponseModel<List<Attend>>> response) {
                ResponseModel<List<Attend>> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(false);
                        if (rspModel.isSucceed()) {
                            List<Attend> attends = rspModel.getResult();
                            calCountNotAttend(attends);
                            mAdapter.replace(attends);
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<List<Attend>>> call, Throwable t) {
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

}
