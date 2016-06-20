package com.me.smartsms.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;

public class ConversationFragment extends BaseFragment {

    private Button editBtn;
    private Button newMsgBtn;
    private Button selectAllBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    private LinearLayout selectMenu;
    private LinearLayout editMenu;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        editBtn = (Button) view.findViewById(R.id.btn_edit);
        newMsgBtn = (Button) view.findViewById(R.id.btn_new_msg);
        selectAllBtn = (Button) view.findViewById(R.id.btn_select_all);
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        deleteBtn = (Button) view.findViewById(R.id.btn_delete);

        selectMenu = (LinearLayout) view.findViewById(R.id.ll_select_menu);
        editMenu = (LinearLayout) view.findViewById(R.id.ll_edit_menu);
        return view;
    }

    @Override
    public void initListener() {
        editBtn.setOnClickListener(this);
        newMsgBtn.setOnClickListener(this);
        selectAllBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {

        //sdk 6.0,需要判断权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 1);
        } else {
            Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/conversations"), null, null, null, null);
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                showSelectMenu();
                break;
            case R.id.btn_cancel:
                showEditMenu();
                break;
            default:
                break;
        }
    }

    private void showSelectMenu() {
        editMenu.animate().translationY(editMenu.getHeight()).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectMenu.animate().translationY(0).setDuration(200);
            }
        }, 200);
    }

    private void showEditMenu() {
        selectMenu.animate().translationY(editMenu.getHeight()).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editMenu.animate().translationY(0).setDuration(200);
            }
        }, 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/conversations"), null, null, null, null);
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                // Permission Denied
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
