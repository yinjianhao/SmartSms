package com.me.smartsms.ui.activity;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.me.smartsms.R;

public class GroupsDetailActivity extends Activity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView tv_title;
    private ListView lv_body;

    private static ListAsyncQueryHandler helper;
    private static ListCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_detail);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleBar_back);
        tv_title = (TextView) findViewById(R.id.tv_titleBar_title);
        lv_body = (ListView) findViewById(R.id.lv_body);
    }

    private void initListener() {
        iv_back.setOnClickListener(this);

        lv_body.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) adapter.getItem(position);
                Intent intent = new Intent(GroupsDetailActivity.this, DetailActivity.class);
                intent.putExtra("address", c.getString(c.getColumnIndex("address")));
                intent.putExtra("thread_id", c.getInt(c.getColumnIndex("_id")));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        String group_name = intent.getStringExtra("group_name");
        String group_id = intent.getStringExtra("group_id");

        helper = new ListAsyncQueryHandler(getContentResolver());
        helper.startQuery(1, null, Uri.parse("content://com.me.smartsms/thread_group/query"), null, "group_id = ?", new String[]{group_id}, null);

        tv_title.setText(group_name);

        adapter = new ListCursorAdapter(this, null, 1);
        lv_body.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_titleBar_back:
                finish();
            default:
                break;
        }
    }

    private static class ListAsyncQueryHandler extends AsyncQueryHandler {

        public ListAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == 1) {

                String selection = "";

                while (cursor.moveToNext()) {
                    selection += "thread_id = " + cursor.getString(cursor.getColumnIndex("thread_id")) + " or ";
                }

                selection = selection.substring(0, selection.length() - 4);

                String[] projection = {
                        "sms.body as snippet",
                        "sms.thread_id as _id",
                        "groups.msg_count as msg_count",
                        "sms.address as address",
                        "sms.date as date"
                };
                helper.startQuery(2, adapter, Uri.parse("content://sms/conversations"), projection, selection, null, "date desc");
            } else if (token == 2) {
                ((ListCursorAdapter) cookie).changeCursor(cursor);
            }
        }
    }

    private class ListCursorAdapter extends CursorAdapter {

        public ListCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.listview_sms_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }

            viewHolder.bodyTv.setText(cursor.getString(cursor.getColumnIndex("snippet")));
            Long date = cursor.getLong(cursor.getColumnIndex("date"));
            if (DateUtils.isToday(date)) {
                viewHolder.timeTv.setText(DateFormat.getTimeFormat(context).format(date));
            } else {
                viewHolder.timeTv.setText(DateFormat.getDateFormat(context).format(date));
            }
        }
    }

    private class ViewHolder {
        public ImageView headImg;
        public TextView nameTv;
        public TextView bodyTv;
        public TextView timeTv;

        public ViewHolder(View view) {
            headImg = (ImageView) view.findViewById(R.id.iv_head);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            bodyTv = (TextView) view.findViewById(R.id.tv_body);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}
