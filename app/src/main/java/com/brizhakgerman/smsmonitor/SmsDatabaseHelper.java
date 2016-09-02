package com.brizhakgerman.smsmonitor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class SmsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sms_table.db";
    private static final int DATABASE_VERSION = 3;

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

    ArrayList<String> getCards() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(true,
                SmsTable.TABLE_SMS,
                new String[] {SmsTable.COLUMN_CARD_NUMBER},
                null, null, null, null, null, null);

        int index = cursor.getColumnIndex(SmsTable.COLUMN_CARD_NUMBER);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(index));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return list;
    }
}
