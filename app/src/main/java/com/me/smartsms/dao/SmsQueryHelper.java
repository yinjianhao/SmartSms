package com.me.smartsms.dao;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

public class SmsQueryHelper extends AsyncQueryHandler{
    public SmsQueryHelper(ContentResolver cr) {
        super(cr);
    }
}
