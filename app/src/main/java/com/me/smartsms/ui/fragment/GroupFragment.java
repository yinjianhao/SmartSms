package com.me.smartsms.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;

public class GroupFragment extends BaseFragment {
    private View view;

    private Button btn;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);

        btn = (Button) view.findViewById(R.id.btn_go_next);
        return view;
    }

    @Override
    public void initListener() {
        btn.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.btn_go_next:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "没有短信权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "有短信权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
