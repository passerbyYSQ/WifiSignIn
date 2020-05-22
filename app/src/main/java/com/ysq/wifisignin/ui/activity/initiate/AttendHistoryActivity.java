package com.ysq.wifisignin.ui.activity.initiate;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.db.Attend;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.Initiate;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendHistoryActivity extends BaseActivity {

    private SimpleDateFormat endTimeSdf = new SimpleDateFormat(" ~ HH:mm:ss");

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private AttendHistoryAdapter mAdapter;


    public static void show(Context context) {
        context.startActivity(new Intent(context, AttendHistoryActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_attend_history;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mRecycler.setAdapter(mAdapter = new AttendHistoryAdapter());

        requestMyAttendHistory();
    }

    class AttendHistoryAdapter extends RecyclerAdapter<Attend> {

        @Override
        protected int getItemViewType(int position, Attend data) {
            return R.layout.item_need_attend_initiate;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<Attend> {
            @BindView(R.id.img_group_portrait)
            CircleImageView mGroupPortrait;
            @BindView(R.id.txt_group_name)
            TextView mGroupName;
            @BindView(R.id.txt_group_creator)
            TextView mGroupCreator;
            @BindView(R.id.txt_initiate_duration)
            TextView mDuration;
            @BindView(R.id.img_go_attend)
            ImageView mIsAttend;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(Attend data) {
                Initiate initiate = data.getInitiate();
                Group group = initiate.getGroup();
                Glide.with(AttendHistoryActivity.this)
                        .load(group.getPhoto())
                        .into(mGroupPortrait);
                mGroupName.setText(group.getGroupName());
                mGroupCreator.setText(initiate.getUser().getUserName());

                String duration = Constant.sdf.format(initiate.getStartTime()) +
                        endTimeSdf.format(initiate.getEndTime());
                mDuration.setText(duration);

                if (data.getStatus().equals(Attend.STATUS_ATTENDED)) {
                    mIsAttend.setImageResource(R.drawable.ic_attended);
                } else {
                    mIsAttend.setImageResource(R.drawable.ic_not_attend);
                }
            }
        }
    }

    public void requestMyAttendHistory() {
        showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Attend>>> call = service.getMyAttendHistory();
        call.enqueue(new Callback<ResponseModel<List<Attend>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Attend>>> call,
                                   Response<ResponseModel<List<Attend>>> response) {
                ResponseModel<List<Attend>> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        if (rspModel.isSucceed()) {
                            mAdapter.replace(rspModel.getResult());
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
                        dismissLoading();
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }
}
