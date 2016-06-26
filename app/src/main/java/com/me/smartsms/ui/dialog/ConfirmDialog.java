package com.me.smartsms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.me.smartsms.R;

public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private OnConfirmListener onConfirmListener;
    private String title;
    private String body;

    public ConfirmDialog(Context context, String title, String body,  OnConfirmListener onConfirmListener) {
        super(context);
        this.onConfirmListener = onConfirmListener;
        this.title = title;
        this.body = body;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView tv_body = (TextView) findViewById(R.id.tv_body);
        tv_body.setText(body);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (onConfirmListener != null) {
                    onConfirmListener.onCancel();
                }
                break;
            case R.id.btn_confirm:
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface OnConfirmListener {
        void onCancel();

        void onConfirm();
    }
}
