package com.brizhakgerman.smsmonitor;

import android.database.sqlite.SQLiteDatabase;

class SmsTable {
    static final String TABLE_SMS = "sms";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TEXT = "text";
    static final String COLUMN_OPERATION_DATE = "operation_date";
    static final String COLUMN_OPERATION_SIGN = "operation_sign";
    static final String COLUMN_CARD_NUMBER = "card_number";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_SMS
            + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " INTEGER NOT NULL, "
            + COLUMN_OPERATION_DATE + " INTEGER, "
            + COLUMN_OPERATION_SIGN + " INTEGER, "
            + COLUMN_CARD_NUMBER + " TEXT, "
            + COLUMN_TEXT + " TEXT NOT NULL"
            + ");";

    private static final String DROP_TABLE_SMS = "DROP TABLE "
            + TABLE_SMS
            + ";";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL(DROP_TABLE_SMS);
            database.execSQL(DATABASE_CREATE);
        }
    }
}
