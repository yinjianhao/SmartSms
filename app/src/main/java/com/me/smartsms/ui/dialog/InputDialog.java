package com.me.smartsms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.me.smartsms.R;


public class InputDialog extends Dialog implements View.OnClickListener {

    private String title;
    private TextView tv_title;
    private TextView tv_body;
    private OnInputDialogListener onInputDialogListener;

    public InputDialog(Context context, String title, OnInputDialogListener onInputDialogListener) {
        super(context);
        this.title = title;
        this.onInputDialogListener = onInputDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input);

        tv_title = (TextView) findViewById(R.id.tv_input_dialog_title);
        tv_title.setText(title);
        tv_body = (TextView) findViewById(R.id.tv_input_dialog_body);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                onInputDialogListener.onCancel();
                break;
            case R.id.btn_confirm:
                onInputDialogListener.onConfirm();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface OnInputDialogListener {
        void onCancel();

        void onConfirm();
    }
}
