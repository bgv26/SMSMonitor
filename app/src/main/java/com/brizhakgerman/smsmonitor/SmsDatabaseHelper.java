package com.brizhakgerman.smsmonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SmsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smstable.db";
    private static final int DATABASE_VERSION = 1;

    SmsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        SmsTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        SmsTable.onUpgrade(database, oldVersion, newVersion);
    }
}
