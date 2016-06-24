package com.me.smartsms.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;

public class GroupFragment extends BaseFragment {
    private View view;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);

        return view;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void processEvents(View v) {

    }
}
