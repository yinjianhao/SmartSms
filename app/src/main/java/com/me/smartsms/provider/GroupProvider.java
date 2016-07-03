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

    {
        String authority = "com.me.smartsms";
        uriMatcher.addURI(authority, "groups/insert", CODE_GROUPS_INSERT);
        uriMatcher.addURI(authority, "groups/query", CODE_GROUPS_QUERY);
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
        Cursor cursor = db.query("groups", projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), Uri.parse("content://com.me.smartsms"));
        return cursor;
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
            default:
                break;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(Uri.parse("content://com.me.smartsms"), null);
        return db.delete("groups", selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(Uri.parse("content://com.me.smartsms"), null);
        return db.update("groups", values, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
