package com.brizhakgerman.smsmonitor;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;


public class SmsContentProvider extends ContentProvider {
    private SmsDatabaseHelper database;

    private static final int MESSAGES = 10;
    private static final int MESSAGE_ID = 20;

    private static final String AUTHORITY = "com.brizhakgerman.smsmonitor";

    private static final String BASE_PATH = "sms";
    static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, MESSAGES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MESSAGE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new SmsDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(SmsTable.TABLE_SMS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MESSAGES:
                break;
            case MESSAGE_ID:
                queryBuilder.appendWhere(SmsTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        switch (uriType) {
            case MESSAGES:
                id = sqlDB.insert(SmsTable.TABLE_SMS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        switch (uriType) {
            case MESSAGES:
                break;
            case MESSAGE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SmsTable.COLUMN_ID + " = " + id;
                } else {
                    selection += " AND" + SmsTable.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        int cnt = sqlDB.delete(SmsTable.TABLE_SMS, selection, selectionArgs);

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // в рамках данной статьи этот метод не пригодится...
        return 0;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                SmsTable.COLUMN_DATE,
                SmsTable.COLUMN_TEXT,
                SmsTable.COLUMN_ID
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
