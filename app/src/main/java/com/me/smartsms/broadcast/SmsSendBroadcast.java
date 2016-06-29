package com.me.smartsms.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsSendBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Activity.RESULT_OK == getResultCode()) {
            Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
        }
    }
}
