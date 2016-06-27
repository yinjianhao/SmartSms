package com.me.smartsms.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseActivity;

public class NewSmsActivity extends Activity {

    private final static String TAG = "NewSmsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sms);

        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.ll1);
//        linearLayout1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick111111");
//            }
//        });
        linearLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch1---" + event.getAction());
                return false;
            }
        });

        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.ll2);
//        linearLayout2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick222222");
//            }
//        });
        linearLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch2---" + event.getAction());
                return false;
            }
        });

        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.ll3);
//        linearLayout3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick33333");
//            }
//        });
        linearLayout3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch3---" + event.getAction());
                return false;
            }
        });
    }
}

