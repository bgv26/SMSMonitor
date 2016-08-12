package com.brizhakgerman.smsmonitor;

import android.database.sqlite.SQLiteDatabase;

class SmsTable {
    static final String TABLE_SMS = "sms";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TEXT = "text";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_SMS
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " INTEGER NOT NULL, "
            + COLUMN_TEXT + " TEXT NOT NULL"
            + ");";

    private static final String DATABASE_DROP = "DROP TABLE IF EXISTS "
            + TABLE_SMS + ";";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL(DATABASE_DROP);
            onCreate(database);
        }
    }
}
