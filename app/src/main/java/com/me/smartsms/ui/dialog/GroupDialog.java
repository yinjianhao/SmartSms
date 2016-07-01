package com.me.smartsms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.me.smartsms.R;

public class GroupDialog extends Dialog implements View.OnClickListener {

    private TextView tv_title;
    private TextView tv_delete;

    private String title;
    private GroupDialogListener listener;

    public GroupDialog(Context context, String title, GroupDialogListener listener) {
        super(context);
        this.title = title;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_group);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete:
                if(listener != null) {
                    listener.onDelete();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface GroupDialogListener {
        void onDelete();
    }
}
