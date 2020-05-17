package com.ysq.wifisignin.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ysq.wifisignin.R;
import com.ysq.wifisignin.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RecyclerAdapter<DataType>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<DataType>>
        implements View.OnClickListener, View.OnLongClickListener,
        AdapterCallback<DataType> {

    private List<DataType> mDataList;
    private AdapterListener<DataType> mListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<DataType> listener) {
        this(new ArrayList<DataType>(), listener);
    }

    public RecyclerAdapter(List<DataType> dataList, AdapterListener<DataType> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    /**
     * 复写默认的布局类型返回
     * @param position
     * @return          类型，其实复写后返回的都是XML文件的id
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局类型
     * @param position
     * @param data      当前的数据
     * @return          XML文件的id，用于创建ViewHolder
     */
    protected abstract int getItemViewType(int position, DataType data);

    /**
     * 创建一个ViewHolder
     * @param parent    RecyclerView
     * @param viewType  界面的类型，约定为xml布局的id
     * @return          ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<DataType> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 得到LayoutInflater用于把XML初始化为View
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 把XML id为viewType的XML文件初始化为root View
        View root = inflater.inflate(viewType, parent, false);
        // 通过子类必须实现的方法，得到一个ViewHolder
        ViewHolder<DataType> holder = onCreateViewHolder(root, viewType);

        // 设置View的Tag为ViewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);

        // 设置事件点击
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        // 进行界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        // 绑定callback
        holder.callback = this;

        return holder;
    }
    /**
     * 得到一个新的ViewHolder
     * @param root      根布局
     * @param viewType  布局的类型（XML的id）
     */
    protected abstract ViewHolder<DataType> onCreateViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个Holder上
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<DataType> holder, int position) {
        // 得到需要绑定的数据
        DataType data = mDataList.get(position);
        // 触发Holder的绑定方法
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 返回整个集合
     * @return
     */
    public List<DataType> getItems() {
        return mDataList;
    }

    /**
     * 插入一条数据并通知插入
     * @param data
     */
    public void add(DataType data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     * @param dataList
     */
    public void add(DataType... dataList) {
        if (dataList != null && dataList.length > 0) {
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(mDataList.size(), dataList.length);
        }
    }

    // 在头部插入
    public void addAtHead(DataType... dataList) {
        if (dataList != null && dataList.length > 0) {
            mDataList.addAll(0, CollectionUtil.toArrayList(dataList));
            notifyItemRangeInserted(0, dataList.length);
        }
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     * @param dataList
     */
    public void add(Collection<DataType> dataList) {
        if (dataList != null && dataList.size() > 0) {
            mDataList.addAll(dataList);
            notifyItemRangeInserted(mDataList.size(), dataList.size());
        }
    }

    /**
     * 全部删除
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一个新的集合
     * @param dataList
     */
    public void replace(Collection<DataType> dataList) {
        mDataList.clear();
        if (dataList == null) {  // 不需要加dataList.size()==0的判断，有些地方不需要这种判断
            return;
        }

        mDataList.addAll(dataList);
        notifyDataSetChanged(); // 通知全部更新
    }

    public void replace(DataType... dataList) {
        replace(CollectionUtil.toArrayList(dataList));
    }

    @Override
    public void update(DataType data, ViewHolder<DataType> holder) {
        int pos = holder.getAdapterPosition();
        if (pos >= 0) {
            mDataList.remove(pos);
            mDataList.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    /**
     *
     * @param view  根布局
     */
    @Override
    public void onClick(View view) {
        // 由于之前通过id绑定，故可以getTag获取根布局里面的ViewHolder
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            // 得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            // 回调方法
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }

    }

    @Override
    public boolean onLongClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            // 得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            // 回调方法
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    /**
     * 设置适配器的监听
     * @param adapterListener
     */
    public void setListener(AdapterListener<DataType> adapterListener) {
        this.mListener = adapterListener;
    }

    /**
     * 自定义的监听器
     * @param <DataType>
     */
    public interface AdapterListener<DataType> {
        // 当Cell点击的时候触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, DataType data);
        // 当Cell长按的时候触发
        void onItemLongClick(RecyclerAdapter.ViewHolder holder, DataType data);
    }

    /**
     * 自定义的ViewHolder
     * @param <DataType>
     */
    public static abstract class ViewHolder<DataType> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback<DataType> callback;
        protected DataType mData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 用于绑定数据的触发
         * @param data  绑定的数据
         */
        void bind(DataType data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 当触发数据绑定时的回调函数，必须复写
         * @param data
         */
        protected abstract void onBind(DataType data);

        /**
         * Holder自己对自己对应的Data进行更新操作
         * @param data
         */
        public void updateData(DataType data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    /**
     * 对接口作一次空实现。那么在可以选择实现其中的一个方法
     * @param <DataType>
     */
    public static class AdapterListenerImpl<DataType> implements AdapterListener<DataType> {
        @Override
        public void onItemClick(ViewHolder holder, DataType data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, DataType data) {

        }
    }

}


