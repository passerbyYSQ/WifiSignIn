package com.ysq.wifisignin.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected View mRoot;
    protected Unbinder mRootUnBinder;

    // 占位布局
    //protected PlaceHolderView mPlaceHolderView;

    // 是否第一次初始化数据
    protected boolean mIsFirstInitData = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // 初始化参数
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            // 初始化当前的根布局，但不在创建时就添加到container里面
            View root = inflater.inflate(layId, container, false);
            initWidget(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                // 把当前控件root从其父控件中移除
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mIsFirstInitData) {
            // 触发一次以后就不会再触发
            mIsFirstInitData = false;
            // 首次初始化数据
            onFirstInit();
        }

        initData(); //当页面初始化之后，再初始化数据
    }

    /**
     * 返回按键触发时调用
     * @return  true：我已经处理返回逻辑，Activity不用自己finish
     *          false：我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 初始化窗口
     * @param bundle
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 获取当前界面的资源文件id。必须由子类实现
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(View root) {
        mRootUnBinder = ButterKnife.bind(this, root);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 当首次初始化数据的时候会被调用
     */
    protected void onFirstInit() {

    }

    /**
     * 设置占位布局
     * @param placeHolderView   继承了占位布局的View
     */
//    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
//        this.mPlaceHolderView = placeHolderView;
//    }


}
