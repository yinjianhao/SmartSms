package com.me.smartsms.ui.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseActivity;

import java.util.List;

public class NewSmsActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "NewSmsActivity";

    private final static int CONTACT_REQUEST_CODE = 1;

    private EditText et_name;

    private EditText et_body;

    private Button btn_send;

    private TextView tv_go_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sms);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name_input);
        et_body = (EditText) findViewById(R.id.et_new_sms_input);
        btn_send = (Button) findViewById(R.id.btn_new_sms);
        tv_go_contact = (TextView) findViewById(R.id.tv_go_contact);
    }

    private void initListener() {
        btn_send.setOnClickListener(this);
        tv_go_contact.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_sms:
                sendSms();
                break;
            case R.id.tv_go_contact:
                goContact();
                break;
            default:
                break;
        }
    }

    private void sendSms() {
        String smsBody = String.valueOf(et_body.getText());
        String address = String.valueOf(et_name.getText());

        if (!smsBody.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            List<String> smsBodys = smsManager.divideMessage(smsBody);
            Intent intent = new Intent("com.me.smartsms.sendsms");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
            for (String sms : smsBodys) {
                smsManager.sendTextMessage(address, null, sms, pendingIntent, null);
            }
        }
    }

    private void goContact() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("vnd.android.cursor.dir/phone");
        startActivityForResult(i, CONTACT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_REQUEST_CODE) {

            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
//                    String number = cursor.getString(cursor.getColumnIndex("number"));
                String number = cursor.getString(cursor.getColumnIndex("data4"));

                for (int i = 0, l = cursor.getColumnCount(); i < l; i++) {
                    Log.d(TAG, cursor.getColumnName(i) + "--->" + cursor.getString(i));
                }

                et_name.setText(number);
                cursor.close();
            }

        }
    }
}

