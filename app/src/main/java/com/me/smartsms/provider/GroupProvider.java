package com.me.smartsms.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.me.smartsms.dao.GroupOpenHelper;


public class GroupProvider extends ContentProvider {

    private GroupOpenHelper helper;
    private SQLiteDatabase db;
    private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final String authority = "com.me.smartsms";

    private final int CODE_GROUPS_INSERT = 0;

    {
        uriMatcher.addURI(authority, "groups/insert", CODE_GROUPS_INSERT);
    }

    @Override
    public boolean onCreate() {
        helper = GroupOpenHelper.getInstance(getContext());
        db = helper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_GROUPS_INSERT:
                long _id = db.insert("groups", null, values);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
