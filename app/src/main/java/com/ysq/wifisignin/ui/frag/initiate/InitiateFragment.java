package com.ysq.wifisignin.ui.frag.initiate;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.api.sign.InitiateModel;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.Initiate;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.util.UiHelper;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 选择在哪个群组发起签到
 * 只有有权限（是创建者或管理员）的群组才会显示出来让你选择
 */
public class InitiateFragment extends BottomSheetDialogFragment {

    private AdminGroupAdapter mAdapter;
    private List<String> items = new ArrayList<>();

    private Context context;

    private String bssid;

    public InitiateFragment(String bssid) {
        super();
        this.bssid = bssid;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 加载布局
        View root = inflater.inflate(R.layout.fragment_group_choose, container, false);

        // 初始化数据
        for (int i = 3; i <= 30; i++) {
            items.add(i + "min");
        }

        // 初始化RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter = new AdminGroupAdapter(
                new RecyclerAdapter.AdapterListenerImpl<Group>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Group data) {
                        showDurationPicker(data);
                    }
                }
        ));

        // 返回布局
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestAdminGroup();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getContext();
    }

    private void showDurationPicker(Group selectedGroup) {
        InitiateFragment.this.dismiss();

        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(),
                new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 , View v) {
                Integer duration = options1 + 3;
                InitiateModel model = new InitiateModel(selectedGroup.getGroupId(), duration, bssid);
                requestInitiate(model, selectedGroup);
            }
        }).build();
        pvOptions.setPicker(items);
        pvOptions.setSelectOptions(2);
        pvOptions.setTitleText("时长");
        pvOptions.show();
    }

    class AdminGroupAdapter extends RecyclerAdapter<Group> {
        public AdminGroupAdapter(AdapterListener<Group> listener) {
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
                Glide.with(InitiateFragment.this)
                        .load(data.getPhoto())
                        .into(mGroupPortrait);
                mGroupName.setText(data.getGroupName());
                mDescription.setText(data.getDescription());
                mIsAdmin.setVisibility(View.GONE);
            }
        }
    }

    // 网络请求
    public void requestAdminGroup() {
        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Group>>> call = service.getAdminGroup();
        call.enqueue(new Callback<ResponseModel<List<Group>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Group>>> call,
                                   Response<ResponseModel<List<Group>>> response) {
                ResponseModel<List<Group>> rspModel = response.body();
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
//                        parent.dismissLoading();
                        if (rspModel.isSucceed()) {
                            List<Group> groups = rspModel.getResult();
                            mAdapter.replace(groups);
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
                        UiHelper.showToast("网络错误");
                    }
                });
            }
        });
    }

    // 发起签到
    public void requestInitiate(InitiateModel model, Group selectedGroup) {
        BaseActivity parent = (BaseActivity) context;
        parent.showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<Initiate>> call = service.initiate(model);
        call.enqueue(new Callback<ResponseModel<Initiate>>() {
            @Override
            public void onResponse(Call<ResponseModel<Initiate>> call,
                                   Response<ResponseModel<Initiate>> response) {
                ResponseModel<Initiate> rspModel = response.body();
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        parent.dismissLoading();
                        if (rspModel.isSucceed()) {
                            Initiate initiate = rspModel.getResult();
                            //UiHelper.showToast(initiate.toString());
                            // 注意！！！此时initiate里面的Group为空
                            initiate.setGroup(selectedGroup);
                            DataListenerAdmin.notifyChanged(InitiateFragment.class,
                                    DataListenerAdmin.ChangedListener.ACTION_ADD, initiate);
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<Initiate>> call, Throwable t) {
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
