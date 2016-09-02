package com.brizhakgerman.smsmonitor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class SmsTable {
    static final String TABLE_SMS = "sms";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TEXT = "text";
    static final String COLUMN_OPERATION_DATE = "operation_date";
    private static final String COLUMN_OPERATION_DATE_INT = "operation_date_int";
    static final String COLUMN_CARD_NUMBER = "card_number";
    private static final String COLUMN_CARD_NUMBER_TEXT = "card_number_text";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_SMS
            + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " INTEGER NOT NULL, "
            + COLUMN_OPERATION_DATE + " INTEGER, "
            + COLUMN_CARD_NUMBER + " TEXT, "
            + COLUMN_TEXT + " TEXT NOT NULL"
            + ");";

    private static final String TABLE_SMS_TMP = "sms_tmp";
    private static final String TEMPORARY_DATABASE_CREATE = "CREATE TABLE "
            + TABLE_SMS_TMP
            + " ("
            + COLUMN_ID + " INTEGER, "
            + COLUMN_DATE + " INTEGER NOT NULL, "
            + COLUMN_OPERATION_DATE + " INTEGER, "
            + COLUMN_CARD_NUMBER + " TEXT, "
            + COLUMN_TEXT + " TEXT NOT NULL"
            + ");";
    private static final String INSERT_TO_TEMPORARY = "INSERT INTO "
            + TABLE_SMS_TMP
            + " SELECT "
            + COLUMN_ID + ", "
            + COLUMN_DATE + ", "
            + COLUMN_OPERATION_DATE_INT + " AS " + COLUMN_OPERATION_DATE + ", "
            + COLUMN_CARD_NUMBER_TEXT + " AS " + COLUMN_CARD_NUMBER + ", "
            + COLUMN_TEXT
            + " FROM " + TABLE_SMS
            + ";";
    private static final String DROP_TABLE_SMS = "DROP TABLE "
            + TABLE_SMS
            + ";";
    private static final String DROP_TABLE_SMS_TMP = "DROP TABLE "
            + TABLE_SMS_TMP
            + ";";
    private static final String INSERT_TO_MAIN = "INSERT INTO "
            + TABLE_SMS
            + " SELECT "
            + COLUMN_ID + ", "
            + COLUMN_DATE + ", "
            + COLUMN_OPERATION_DATE + ", "
            + COLUMN_CARD_NUMBER + ", "
            + COLUMN_TEXT
            + " FROM " + TABLE_SMS_TMP
            + ";";

    private static final String ADD_COLUMN_OPERATION_DATE = "ALTER TABLE "
            + TABLE_SMS
            + " ADD COLUMN " + COLUMN_OPERATION_DATE_INT + " INTEGER;";
    private static final String ADD_COLUMN_CARD_NUMBER = "ALTER TABLE "
            + TABLE_SMS
            + " ADD COLUMN " + COLUMN_CARD_NUMBER_TEXT + " TEXT;";

    static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == 2 && newVersion == 3) {
            database.execSQL(ADD_COLUMN_OPERATION_DATE);
            database.execSQL(ADD_COLUMN_CARD_NUMBER);
            FillNewFields(database);
            RenewDatabase(database);
        }
    }

    private static void FillNewFields(SQLiteDatabase database) {
        ContentValues cv = new ContentValues();
        Cursor c = database.query(TABLE_SMS, new String[]{COLUMN_ID, COLUMN_OPERATION_DATE, COLUMN_CARD_NUMBER}, null, null, null, null, null);
        int indexColumnId = c.getColumnIndex(COLUMN_ID);
        int indexOperationDate = c.getColumnIndex(COLUMN_OPERATION_DATE);
        int indexCardNumber = c.getColumnIndex(COLUMN_CARD_NUMBER);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(indexColumnId);
                String date = c.getString(indexOperationDate);
                int cardNumber = c.getInt(indexCardNumber);
                cv.clear();
                cv.put(COLUMN_CARD_NUMBER_TEXT, String.valueOf(cardNumber));
                cv.put(COLUMN_OPERATION_DATE_INT, (new SimpleDate(date)).toLong());
                database.update(TABLE_SMS, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            } while (c.moveToNext());
        }
        c.close();
    }

    private static void RenewDatabase(SQLiteDatabase database) {
        database.execSQL(TEMPORARY_DATABASE_CREATE);
        database.execSQL(INSERT_TO_TEMPORARY);
        database.execSQL(DROP_TABLE_SMS);
        database.execSQL(DATABASE_CREATE);
        database.execSQL(INSERT_TO_MAIN);
        database.execSQL(DROP_TABLE_SMS_TMP);
    }
}
