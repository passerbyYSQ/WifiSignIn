package com.ysq.wifisignin.ui.frag;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ysq.wifisignin.R;
import com.ysq.wifisignin.data.Constant;
import com.ysq.wifisignin.ui.activity.user.RegisterActivity;

/**
 *  查看隐私条款的Fragment
 */
public class PrivacyFragment extends BottomSheetDialogFragment {

    public static void show(FragmentManager manager) {
        new PrivacyFragment().show(manager, PrivacyFragment.class.getName());
    }

    public PrivacyFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局中的控件
        View root = inflater.inflate(R.layout.fragment_privacy, container, false);

        WebView webView = root.findViewById(R.id.web_privacy);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Constant.PRIVACY_URL);

        Button agree = root.findViewById(R.id.btn_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyFragment.this.dismiss();
                RegisterActivity registerActivity = (RegisterActivity) getActivity();
                registerActivity.getmAgree().setChecked(true);
            }
        });

        return root;
    }
}
