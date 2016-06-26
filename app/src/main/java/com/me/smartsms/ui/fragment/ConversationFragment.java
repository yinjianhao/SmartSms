package com.me.smartsms.ui.fragment;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.me.smartsms.R;
import com.me.smartsms.base.BaseFragment;
import com.me.smartsms.ui.activity.DetailActivity;
import com.me.smartsms.ui.activity.NewSmsActivity;
import com.me.smartsms.ui.dialog.ConfirmDialog;
import com.me.smartsms.ui.dialog.DeleteDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    private String phoneNum;
    private ViewHolder outerViewHolder;

    //编辑状态
    private Boolean editState = false;
    private Boolean isAllChecked = false;
    private static Cursor smsCursor;
    private static List<Integer> idsList = new ArrayList<>();

    private static final int WHAT_DELETE_COMPLETE = 1;
    private static final int WHAT_UPDATE_DELETE_PROGRESS = 2;

    private DeleteDialog deleteDialog;
    private Boolean isStopDelete = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DELETE_COMPLETE:
                    editState = false;
                    smsCursorAdapter.notifyDataSetChanged();
                    showEditMenu();
                    deleteDialog.dismiss();
                    break;
                case WHAT_UPDATE_DELETE_PROGRESS:
                    deleteDialog.setProgress(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editState) {
                    smsCursor.moveToPosition(position);
                    int _id = smsCursor.getInt(smsCursor.getColumnIndex("_id"));
                    if (idsList.contains(_id)) {
                        idsList.remove((Integer) _id);
                        isAllChecked = false;
                    } else {
                        idsList.add(_id);
                        if (smsCursor.getCount() == idsList.size()) {
                            isAllChecked = true;
                        }
                    }
                    smsCursorAdapter.notifyDataSetChanged();
                } else {
                    Cursor c = (Cursor) smsCursorAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("address", c.getString(c.getColumnIndex("address")));
                    intent.putExtra("thread_id", c.getInt(c.getColumnIndex("_id")));
                    startActivity(intent);
                }
            }
        });
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
                editState = true;
                smsCursorAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_cancel:
                showEditMenu();
                editState = false;
                smsCursorAdapter.notifyDataSetChanged();
                idsList.clear();
                isAllChecked = false;
                break;
            case R.id.btn_select_all:
                isAllChecked = !isAllChecked;
                idsList.clear();
                if (isAllChecked) {
                    smsCursor.moveToFirst();
                    do {
                        idsList.add(smsCursor.getInt(smsCursor.getColumnIndex("_id")));
                    } while (smsCursor.moveToNext());
                }
                smsCursorAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_delete:
                if (idsList.isEmpty()) {
                    Toast.makeText(getActivity(), "请选择要删除的条目", Toast.LENGTH_SHORT).show();
                } else {
                    ConfirmDialog confirmDialog = new ConfirmDialog(getActivity(), "提示", "确定要删除吗?", new ConfirmDialog.OnConfirmListener() {
                        @Override
                        public void onCancel() {
                            isStopDelete = true;
                        }

                        @Override
                        public void onConfirm() {
                            final int maxLength = idsList.size();
                            deleteDialog = new DeleteDialog(getActivity(), maxLength, new DeleteDialog.OnDeleteCancelListener() {
                                @Override
                                public void onCancel(View v) {
                                    isStopDelete = true;
                                }
                            });
                            deleteDialog.show();
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < maxLength; i++) {
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (isStopDelete) {
                                            isStopDelete = false;
                                            break;
                                        }
                                        getActivity().getContentResolver().delete(Uri.parse("content://sms"), "thread_id = ?", new String[]{String.valueOf(idsList.get(i))});
                                        Message message = new Message();
                                        message.what = WHAT_UPDATE_DELETE_PROGRESS;
                                        message.arg1 = i + 1;
                                        handler.sendMessage(message);
                                    }
                                    idsList.clear();
                                    handler.sendEmptyMessage(WHAT_DELETE_COMPLETE);
                                }
                            };
                            thread.start();
                        }
                    });
                    confirmDialog.show();
                }
                break;
            case R.id.btn_new_msg:
                Intent intent = new Intent(getActivity(), NewSmsActivity.class);
                startActivity(intent);
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

        //获取联系人名字
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

        //获取联系人头像
        if (!_id.equals("")) {
            InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(
                    getActivity().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(_id)));
            if (in != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                outerViewHolder.headImg.setImageBitmap(bitmap);
//                outerViewHolder.headImg.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else {
                outerViewHolder.headImg.setImageResource(R.drawable.img_default_avatar);
            }
        } else {
            outerViewHolder.headImg.setImageResource(R.drawable.img_default_avatar);
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
                smsCursor = cursor;
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {

        }
    }

    class SmsCursorAdapter extends CursorAdapter {

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

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

            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            if (editState) {
                viewHolder.checkboxImg.setVisibility(View.VISIBLE);
                if (isAllChecked) {
                    viewHolder.checkboxImg.setImageResource(R.drawable.common_checkbox_checked);
                } else {
                    if (idsList.contains(_id)) {
                        viewHolder.checkboxImg.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        viewHolder.checkboxImg.setImageResource(R.drawable.common_checkbox_normal);
                    }
                }
            } else {
                viewHolder.checkboxImg.setVisibility(View.GONE);
            }
        }
    }

    class ViewHolder {
        public ImageView headImg;
        public TextView nameTv;
        public TextView bodyTv;
        public TextView timeTv;
        private ImageView checkboxImg;

        public ViewHolder(View view) {
            headImg = (ImageView) view.findViewById(R.id.iv_head);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            bodyTv = (TextView) view.findViewById(R.id.tv_body);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            checkboxImg = (ImageView) view.findViewById(R.id.iv_checkbox);
        }
    }
}
