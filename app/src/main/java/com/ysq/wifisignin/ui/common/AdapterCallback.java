package com.ysq.wifisignin.ui.common;

/**
 * @author passerbyYSQ
 * @create 2020-05-14 10:16
 */
public interface AdapterCallback<DataType> {
    void update(DataType data, RecyclerAdapter.ViewHolder<DataType> holder);
}
