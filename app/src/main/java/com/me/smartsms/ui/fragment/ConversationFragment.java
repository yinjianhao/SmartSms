package com.me.smartsms.ui.fragment;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;

import java.io.InputStream;

public class ConversationFragment extends BaseFragment {

    private Button editBtn;
    private Button newMsgBtn;
    private Button selectAllBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    private LinearLayout selectMenu;
    private LinearLayout editMenu;

    private ListView listView;

    private AsyncQueryHandler asyncQueryHandler;
    private Uri smsConversationsUri;
    private SmsCursorAdapter smsCursorAdapter;

    private static final int PERMISSION_RECEIVE_SMS = 1;
    private static final int PERMISSION_RECEIVE_CONTACTS = 2;

    private String phoneNum;
    private ViewHolder outerViewHolder;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        Log.d("bbb", String.valueOf(view.getId()));

        editBtn = (Button) view.findViewById(R.id.btn_edit);
        newMsgBtn = (Button) view.findViewById(R.id.btn_new_msg);
        selectAllBtn = (Button) view.findViewById(R.id.btn_select_all);
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        deleteBtn = (Button) view.findViewById(R.id.btn_delete);

        selectMenu = (LinearLayout) view.findViewById(R.id.ll_select_menu);
        editMenu = (LinearLayout) view.findViewById(R.id.ll_edit_menu);

        listView = (ListView) view.findViewById(R.id.lv_sms_content);
        return view;
    }

    @Override
    public void initListener() {
        editBtn.setOnClickListener(this);
        newMsgBtn.setOnClickListener(this);
        selectAllBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        asyncQueryHandler = new SmsQueryHelper(getActivity().getContentResolver());
        smsConversationsUri = Uri.parse("content://sms/conversations");

        smsCursorAdapter = new SmsCursorAdapter(getActivity(), null, 1);
        listView.setAdapter(smsCursorAdapter);

        //6.0,需要判断权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            operationToSmsRead();
        }
    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                showSelectMenu();
                break;
            case R.id.btn_cancel:
                showEditMenu();
                break;
            default:
                break;
        }
    }

    private void showSelectMenu() {
        editMenu.animate().translationY(editMenu.getHeight()).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectMenu.animate().translationY(0).setDuration(200);
            }
        }, 200);
    }

    private void showEditMenu() {
        selectMenu.animate().translationY(editMenu.getHeight()).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editMenu.animate().translationY(0).setDuration(200);
            }
        }, 200);
    }

    public void operationToSmsRead() {
        String[] projection = {
                "sms.body as snippet",
                "sms.thread_id as _id",
                "groups.msg_count as msg_count",
                "sms.address as address",
                "sms.date as date"
        };
        asyncQueryHandler.startQuery(1, smsCursorAdapter, smsConversationsUri, projection, null, null, "date desc");
    }

    public void operationToContactRead() {
        String[] projection = {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID
        };

        String _id = "";

        Cursor c = getActivity().getContentResolver().query(Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNum), projection, null, null, null);

        if (c != null) {
            if (c.getCount() != 0) {
                c.moveToFirst();
                outerViewHolder.nameTv.setText(c.getString(0));
                _id = c.getString(1);
            } else {
                outerViewHolder.nameTv.setText(phoneNum);
            }
            c.close();
        } else {
            outerViewHolder.nameTv.setText(phoneNum);
        }

        if(!_id.equals("")) {
            InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(), Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, _id));
            outerViewHolder.headImg.setImageBitmap(BitmapFactory.decodeStream(in));
        }
    }

    static class SmsQueryHelper extends AsyncQueryHandler {

        public SmsQueryHelper(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

            if (cookie != null && cookie instanceof SmsCursorAdapter && cursor != null && cursor.getCount() > 0) {
                ((SmsCursorAdapter) cookie).changeCursor(cursor);
            }
        }
    }

    class SmsCursorAdapter extends CursorAdapter {

        public SmsCursorAdapter(Context context, Cursor c, int flags) {
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

            outerViewHolder = viewHolder;
            phoneNum = cursor.getString(cursor.getColumnIndex("address"));

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                operationToContactRead();
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

    class ViewHolder {
        public ImageView headImg;
        public TextView nameTv;
        public TextView bodyTv;
        public TextView timeTv;

        public ViewHolder(View view) {
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            bodyTv = (TextView) view.findViewById(R.id.tv_body);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}
