package com.me.smartsms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.me.smartsms.R;

public class GroupDialog extends Dialog {

    private Context context;
    private String title;
    private String[] items;
    private GroupDialogListener listener;

    public GroupDialog(Context context, String title, String[] items, GroupDialogListener listener) {
        super(context);
        this.context = context;
        this.title = title;
        this.listener = listener;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_group);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);

        ListView lv_groups_operation = (ListView) findViewById(R.id.lv_groups_operation);
        GroupDialogAdapter groupDialogAdapter = new GroupDialogAdapter();
        lv_groups_operation.setAdapter(groupDialogAdapter);

        lv_groups_operation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemClickListener(parent, view, position, id);
                dismiss();
            }
        });
    }

    public interface GroupDialogListener {
        void onItemClickListener(AdapterView<?> parent, View view, int position, long id);
    }

    private class GroupDialogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.listview_dialog_group_item, parent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tv_name.setText(items[position]);
            return view;
        }

        private class ViewHolder {

            public TextView tv_name;

            public ViewHolder(View view) {
                tv_name = (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }
}
