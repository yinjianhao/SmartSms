package com.me.smartsms.ui.activity;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.me.smartsms.R;

public class DetailActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";
    private String address;
    private int thread_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        initListener();
        initData();
    }

    private void initView() {

    }

    private void initListener() {

    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            address = intent.getStringExtra("address");
            thread_id = intent.getIntExtra("thread_id", -1);

            initTitleBar();

            SmsDetailQueryHelper smsDetailQueryHelper = new SmsDetailQueryHelper(getContentResolver());
            String[] projection = {
                    "_id",
                    "body",
                    "type",
                    "date"
            };
            smsDetailQueryHelper.startQuery(1, null, Uri.parse("content://sms"), projection, "thread_id =" + thread_id, null, "date");
        }
    }

    private void initTitleBar() {
        findViewById(R.id.iv_titleBar_back).setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_titleBar_title);

        String name = findNameByPhone();
        tv_title.setText(name != null ? name : address);
    }

    private String findNameByPhone() {
        String name = null;

        String[] projection = {
                ContactsContract.PhoneLookup.DISPLAY_NAME
        };

        //获取联系人名字
        Cursor c = getContentResolver().query(Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address), projection, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            name = c.getString(0);
            c.close();
        }

        return name;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_titleBar_back:
                finish();
                break;
            default:
                break;
        }
    }

    private static class SmsDetailQueryHelper extends AsyncQueryHandler {
        public SmsDetailQueryHelper(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            while (cursor.moveToNext()) {
                Log.d(TAG, cursor.getString(1));
            }
        }
    }
}
