package com.me.smartsms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.me.smartsms.R;

public class DeleteDialog extends Dialog implements View.OnClickListener {
    private TextView tv_title;
    private ProgressBar pb_delete_num;
    private OnDeleteCancelListener onDeleteCancelListener;
    private int maxProgress = 0;

    public DeleteDialog(Context context, int maxProgress, OnDeleteCancelListener onDeleteCancelListener) {
        super(context);
        this.onDeleteCancelListener = onDeleteCancelListener;
        this.maxProgress = maxProgress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("正在删除(" + 0 + "/" + maxProgress + ")");
        pb_delete_num = (ProgressBar) findViewById(R.id.pb_delete_num);
        pb_delete_num.setMax(maxProgress);

        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                onDeleteCancelListener.onCancel(v);
                break;
            default:
                break;
        }
    }

    public void setProgress(int progress) {
        tv_title.setText("正在删除(" + progress + "/" + maxProgress + ")");
        pb_delete_num.setProgress(progress);
    }

    public interface OnDeleteCancelListener {
        void onCancel(View v);
    }
}
