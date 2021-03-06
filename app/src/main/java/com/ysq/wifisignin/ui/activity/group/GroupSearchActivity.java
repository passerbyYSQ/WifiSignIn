package com.ysq.wifisignin.ui.activity.group;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mylhyl.circledialog.CircleDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.bean.ResponseModel;
import com.ysq.wifisignin.bean.db.Group;
import com.ysq.wifisignin.bean.db.SearchHistory;
import com.ysq.wifisignin.bean.db.SearchHistory_Table;
import com.ysq.wifisignin.data.DataListenerAdmin;
import com.ysq.wifisignin.net.NetWork;
import com.ysq.wifisignin.net.RemoteService;
import com.ysq.wifisignin.ui.common.BaseActivity;
import com.ysq.wifisignin.ui.common.RecyclerAdapter;
import com.ysq.wifisignin.ui.frag.group.GroupSearchFragment;
import com.ysq.wifisignin.util.UiHelper;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupSearchActivity extends BaseActivity
        implements QueryTransaction.QueryResultListCallback<SearchHistory> {

    @BindView(R.id.edit_keywords)
    EditText mKeywordsEdit;  // 输入框
    @BindView(R.id.img_clear_content)
    ImageView mClearKeywords;  // 清除输入内容的小叉叉
    @BindView(R.id.lay_history)
    LinearLayout mLayHistory;  // 装输入历史的容器布局

    @BindView(R.id.recycler_history)
    RecyclerView mRecyclerHistory;

    private HistoryAdapter mHistoryAdapter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupSearchActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 监听输入框输入变化
        mKeywordsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keywords = editable.toString().trim();
                int isVisible = !TextUtils.isEmpty(keywords) ? View.VISIBLE : View.INVISIBLE;
                // 根据输入内容是否为空，设置小叉叉是否可见
                mClearKeywords.setVisibility(isVisible);

                // 输入一变化，就去模糊查询，更新界面
                loadHistories();
            }
        });

        mKeywordsEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClick();
                }
                return true;
            }
        });

        // 初始化RecyclerView相关
        mRecyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerHistory.setAdapter(
                mHistoryAdapter = new HistoryAdapter(
                        new RecyclerAdapter.AdapterListenerImpl<SearchHistory>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, SearchHistory data) {
                        // 点击某条搜索历史，就将这条搜素历史set到输入框。
                        mKeywordsEdit.setText(data.getKeywords());
                        // 把光标设置在末尾
                        mKeywordsEdit.requestFocus();
                        mKeywordsEdit.setSelection(mKeywordsEdit.getText().toString().length());
                    }
                }));

        // 初始化界面的时候就将输入框的内容置为空字符串，让它搜索近10条记录
        // 否则不会去搜索
        mKeywordsEdit.setText("");

        mKeywordsEdit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    loadHistories();
                    mLayHistory.setVisibility(View.VISIBLE);
                }
            }
        });


        // 动态加载fragment
        GroupSearchFragment groupSearchFragment = new GroupSearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_group_search_result, groupSearchFragment)
                .commit();
    }


    // 点击小叉叉清除输入框内容
    @OnClick(R.id.img_clear_content)
    void onClearKeywordsClick() {
        mKeywordsEdit.setText("");
        //mKeywordsEdit.requestFocus();
    }

    // 点击返回按钮，finish掉当前页面
    @OnClick(R.id.img_return)
    void onReturnClick() {
        finish();
    }

    @OnClick(R.id.txt_hide_history)
    void onHideHistory() {
        mLayHistory.setVisibility(View.INVISIBLE);
        mKeywordsEdit.clearFocus();  // 失去焦点，会触发监听，隐藏搜索历史
    }

    // 点击清空历史记录
    @OnClick(R.id.lay_clear_all)
    void onClearAllClick() {
        // 提醒对话框
        new CircleDialog.Builder()
                .setTitle("提醒")
                .setText("是否确认清空所有搜索记录？")//内容
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 清空数据库的所有记录
                        SQLite.delete().from(SearchHistory.class)
                                .async()
                                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<SearchHistory>() {
                                    @Override
                                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<SearchHistory> tResult) {
                                        // 通知界面更新
                                        // 重新查询集合，显示到界面
                                        loadHistories();
                                    }
                                })
                                .execute();
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    // 点击搜索
    @OnClick(R.id.txt_search)
    void onSearchClick() {
        String keywords = mKeywordsEdit.getText().toString().trim();
        SearchHistory history = new SearchHistory();
        history.setKeywords(keywords);
        history.setUpdateAt(new Date());
        history.setType(SearchHistory.TYPE_GROUP);

        // 如果没有，就插入数据；如果已存在，就更新updateAt
        history.save();

        // 隐藏搜索历史框和软键盘
        //loadHistories(); // 隐藏之前先重新Load一遍，更新一下
        onHideHistory();

        //mLayHistory.setVisibility(View.INVISIBLE);
        //mKeywordsEdit.setText(""); // 测试用
        //loadHistories();

        // 进行网络请求
        //UiHelper.showToast("网络请求查询结果");
        requestSearchGroup(keywords);
    }

    // 根据时间倒序查询
    // 查询最近的10条
    // 异步查询
    private void loadHistories() {
        String keywords = mKeywordsEdit.getText().toString().trim();
        if (TextUtils.isEmpty(keywords)) {
            keywords = "";
        }
        String keywordsLike = "%" + keywords + "%";

        SQLite.select()
                .from(SearchHistory.class)
                .where(SearchHistory_Table.keywords.like(keywordsLike))
                .orderBy(SearchHistory_Table.updateAt, false)
                .limit(10)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction,
                                  @NonNull List<SearchHistory> tResult) {
        // 异步查询返回的结果，显示到界面去
        // 简单粗暴，直接替换整个数据源。replace里面已经做了notify了
        mHistoryAdapter.replace(tResult);
    }

    class HistoryAdapter extends RecyclerAdapter<SearchHistory> {
        public HistoryAdapter(AdapterListener<SearchHistory> listener) {
            super(listener);
        }

        @Override
        protected int getItemViewType(int position, SearchHistory data) {
            // 绑定子项布局
            return R.layout.item_search_history;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new ViewHolder(root);
        }

        class ViewHolder extends RecyclerAdapter.ViewHolder<SearchHistory> {
            @BindView(R.id.txt_keywords)
            TextView itemKeywords;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(SearchHistory data) {
                itemKeywords.setText(data.getKeywords());
            }

            @OnClick(R.id.img_remove)
            void onRemoveClick() {
                // 在数据库中清除此条目
                // mData为当前子项所对应的bean
                boolean isSucceed = mData.delete();
                if (isSucceed) {
                    // 并通知Recycler里面的数据源，更新界面
                    // 由于显示的数据不是很多，直接通知更新整个集合
                    UiHelper.showToast("删除成功");
                    // 直接重新查询一遍
                    loadHistories();
                }
            }
        }
    }

    public void requestSearchGroup(String keywords) { // 界面已经控制好了，不可能为null，但允许为空字符串
        showLoading();

        RemoteService service = NetWork.remote();
        Call<ResponseModel<List<Group>>> call = service.searchGroup(keywords);
        call.enqueue(new Callback<ResponseModel<List<Group>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<Group>>> call,
                                   Response<ResponseModel<List<Group>>> response) {
                ResponseModel<List<Group>> rspModel = response.body();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 隐藏Loading
                        dismissLoading();
                        if (rspModel.isSucceed()) {
                            // 回调数据给GroupSearchFragment
                            List<Group> groups = rspModel.getResult();
                            DataListenerAdmin.notifyChanged(GroupSearchActivity.class,
                                    DataListenerAdmin.ChangedListener.ACTION_ADD,
                                    groups.toArray(new Group[0])); // List => 数组
                        } else {
                            UiHelper.showToast(rspModel.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseModel<List<Group>>> call, Throwable t) {
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
