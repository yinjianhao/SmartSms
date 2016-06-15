package com.me.smartsms.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 基类activity
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }

    public abstract void initView();
    public abstract void initListener();
    public abstract void initData();
    public abstract void processEvents(View v);

    @Override
    public void onClick(View v) {
        processEvents(v);
    }
}
