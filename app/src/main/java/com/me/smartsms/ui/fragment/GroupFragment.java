package com.me.smartsms.ui.fragment;


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;
import com.me.smartsms.ui.dialog.GroupDialog;
import com.me.smartsms.ui.dialog.InputDialog;

public class GroupFragment extends BaseFragment {
    private final String TAG = "GroupFragment";
    private View view;
    private ListView lv_groups;
    private Button btn_new_group;

    private GroupsCursorAdapter adapter;
    private GroupsAsyncQueryHandler helper;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        lv_groups = (ListView) view.findViewById(R.id.lv_groups);
        btn_new_group = (Button) view.findViewById(R.id.btn_new_group);
        return view;
    }

    @Override
    public void initListener() {
        btn_new_group.setOnClickListener(this);
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "单击", Toast.LENGTH_SHORT).show();
            }
        });

        lv_groups.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Cursor c = (Cursor) adapter.getItem(position);
                final String _id = c.getString(c.getColumnIndex("_id"));

                GroupDialog groupDialog = new GroupDialog(getActivity(), "操作", new String[]{"删除", "改名"}, new GroupDialog.GroupDialogListener() {
                    @Override
                    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                helper.startDelete(1, null, Uri.parse("content://com.me.smartsms"), "_id = ?", new String[]{_id});
                                break;
                            case 1:
                                InputDialog inputDialog = new InputDialog(getActivity(), "修改组名", new InputDialog.OnInputDialogListener() {

                                    @Override
                                    public void onCancel() {

                                    }

                                    @Override
                                    public void onConfirm(String groupName) {
                                        ContentValues values = new ContentValues();
                                        values.put("name", groupName);
                                        helper.startUpdate(1, null, Uri.parse("content://com.me.smartsms"), values, "_id = ?", new String[]{_id});
                                    }
                                });
                                inputDialog.show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                groupDialog.show();
                return true;
            }
        });
    }

    @Override
    public void initData() {
        adapter = new GroupsCursorAdapter(getActivity(), null, 1);
        lv_groups.setAdapter(adapter);

        helper = new GroupsAsyncQueryHandler(getActivity().getContentResolver());
        helper.startQuery(1, adapter, Uri.parse("content://com.me.smartsms/groups/query"), null, null, null, "create_date desc");
    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.btn_new_group:
                InputDialog inputDialog = new InputDialog(getActivity(), "新建群组", new InputDialog.OnInputDialogListener() {
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "cancel");
                    }

                    @Override
                    public void onConfirm(String groupName) {
                        ContentValues values = new ContentValues();
                        values.put("name", groupName);
                        values.put("create_date", System.currentTimeMillis());
                        values.put("thread_count", 0);
                        helper.startInsert(1, null, Uri.parse("content://com.me.smartsms/groups/insert"), values);
                    }
                });
                inputDialog.show();
                break;
            default:
                break;
        }
    }

    private static class GroupsAsyncQueryHandler extends AsyncQueryHandler {

        public GroupsAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            ((GroupsCursorAdapter) cookie).changeCursor(cursor);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {

        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {

        }
    }

    private class GroupsCursorAdapter extends CursorAdapter {


        public GroupsCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.listview_group_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }

            viewHolder.tv_group_name.setText(cursor.getString(cursor.getColumnIndex("name")) + "(" + cursor.getString(cursor.getColumnIndex("thread_count")) + ")");
            long date = cursor.getLong(cursor.getColumnIndex("create_date"));
            if (DateUtils.isToday(date)) {
                viewHolder.tv_date.setText(DateFormat.getTimeFormat(getActivity()).format(date));
            } else {
                viewHolder.tv_date.setText(DateFormat.getDateFormat(getActivity()).format(date));
            }
        }
    }

    private class ViewHolder {

        public TextView tv_group_name;
        public TextView tv_date;

        public ViewHolder(View view) {
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}
