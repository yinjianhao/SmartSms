package com.me.smartsms.ui.fragment;


import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;
import com.me.smartsms.ui.dialog.InputDialog;

public class GroupFragment extends BaseFragment {
    private final String TAG = "GroupFragment";
    private View view;
    private ListView lv_groups;
    private Button btn_new_group;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        lv_groups = (ListView) view.findViewById(R.id.lv_groups);
        btn_new_group = (Button) view.findViewById(R.id.btn_new_group);
        return view;
    }

    @Override
    public void initListener() {
        btn_new_group.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.btn_new_group:
                InputDialog inputDialog = new InputDialog(getActivity(), "新建群组", new InputDialog.OnInputDialogListener() {
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "cancel");
                    }

                    @Override
                    public void onConfirm() {
                        Log.d(TAG, "onConfirm");
                    }
                });
                inputDialog.show();

                ContentValues values = new ContentValues();
                values.put("name", "帅的人还在码代码");
                values.put("create_date", System.currentTimeMillis());
                values.put("thread_count", 0);
                getActivity().getContentResolver().insert(Uri.parse("content://com.me.smartsms/groups/insert"), values);
                break;
            default:
                break;
        }
    }
}
