package com.me.smartsms.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.me.smartsms.dao.GroupOpenHelper;

public class GroupProvider extends ContentProvider {

    private SQLiteDatabase db;
    private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private final int CODE_GROUPS_INSERT = 0;
    private final int CODE_GROUPS_QUERY = 1;
    private final int CODE_GROUPS_DELETE = 2;
    private final int CODE_GROUPS_UPDATE = 3;
    private final int CODE_THREAD_GROUP_INSERT = 4;
    private final int CODE_THREAD_GROUP_QUERY = 5;
    private final int CODE_THREAD_GROUP_DELETE = 6;
    private final int CODE_THREAD_GROUP_UPDATE = 7;

    {
        String authority = "com.me.smartsms";
        uriMatcher.addURI(authority, "groups/insert", CODE_GROUPS_INSERT);
        uriMatcher.addURI(authority, "groups/query", CODE_GROUPS_QUERY);
        uriMatcher.addURI(authority, "groups/delete", CODE_GROUPS_DELETE);
        uriMatcher.addURI(authority, "groups/update", CODE_GROUPS_UPDATE);
        uriMatcher.addURI(authority, "thread_group/insert", CODE_THREAD_GROUP_INSERT);
        uriMatcher.addURI(authority, "thread_group/query", CODE_THREAD_GROUP_QUERY);
        uriMatcher.addURI(authority, "thread_group/delete", CODE_THREAD_GROUP_DELETE);
        uriMatcher.addURI(authority, "thread_group/update", CODE_THREAD_GROUP_UPDATE);
    }

    @Override
    public boolean onCreate() {
        GroupOpenHelper helper = GroupOpenHelper.getInstance(getContext());
        db = helper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case CODE_GROUPS_QUERY:
                Cursor cursor = db.query("groups", projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), Uri.parse("content://com.me.smartsms"));
                return cursor;
            case CODE_THREAD_GROUP_QUERY:
                cursor = db.query("thread_group", projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_GROUPS_INSERT:
                long _id = db.insert("groups", null, values);
                if (_id != -1) {
                    getContext().getContentResolver().notifyChange(Uri.parse("content://com.me.smartsms"), null);
                    return ContentUris.withAppendedId(uri, _id);
                }
                break;
            case CODE_THREAD_GROUP_INSERT:
                _id = db.insert("thread_group", null, values);
                if (_id != -1) {
                    return ContentUris.withAppendedId(uri, _id);
                }
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int id;
        switch (uriMatcher.match(uri)) {
            case CODE_GROUPS_DELETE:
                getContext().getContentResolver().notifyChange(Uri.parse("content://com.me.smartsms"), null);
                id = db.delete("groups", selection, selectionArgs);
                return id;
            case CODE_THREAD_GROUP_DELETE:
                id = db.delete("thread_group", selection, selectionArgs);
                return id;
            default:
                break;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int id;
        switch (uriMatcher.match(uri)) {
            case CODE_GROUPS_UPDATE:
                getContext().getContentResolver().notifyChange(Uri.parse("content://com.me.smartsms"), null);
                id = db.update("groups", values, selection, selectionArgs);
                return id;
            case CODE_THREAD_GROUP_UPDATE:
                id = db.update("thread_group", values, selection, selectionArgs);
                return id;
            default:
                break;
        }
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
