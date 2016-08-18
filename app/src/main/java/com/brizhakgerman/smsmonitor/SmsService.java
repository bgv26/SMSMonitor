package com.brizhakgerman.smsmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsService extends Service {

    private AppWidgetManager appWidgetManager;
    private SharedPreferences sharedPreference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainFragment.class), 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(R.mipmap.ic_launcher, notification);
    }

    private void updateWidget(SmsData data) {
        appWidgetManager = AppWidgetManager.getInstance(this);
        sharedPreference = getSharedPreferences(MyWidget.WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        int widgetID = findWidgetIDbyCardNumber(data.cardNumber);
        if (widgetID != 0) {
            editor.putString(MyWidget.WIDGET_BALANCE_TEXT + widgetID,
                    data.balance + "р");
            editor.putString(MyWidget.WIDGET_LAST_OPERATION_TEXT + widgetID,
                    data.datetime + " " + data.amount + "р");
            editor.putBoolean(MyWidget.WIDGET_LAST_OPERATION_SIGN + widgetID,
                    data.operationSign);
            editor.apply();

            MyWidget.updateWidget(this, appWidgetManager, sharedPreference, widgetID);
        }
    }

    private int findWidgetIDbyCardNumber(int cardNumber) {
        int[] ids = appWidgetManager.getAppWidgetIds(
                new ComponentName(getPackageName(), MyWidget.class.getCanonicalName()));
        for (int id : ids) {
            int curCardNumber = sharedPreference.getInt(MyWidget.WIDGET_CARD_NUMBER_TEXT + id, 0);
            if (curCardNumber != 0 && curCardNumber == cardNumber)
                return id;
        }
        return 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms_body = intent.getExtras().getString(SmsMonitor.SMS_BODY);
        showNotification(sms_body);
        saveSms(sms_body);

        SmsData event = processSms(sms_body);
        if (event != null) {
            updateWidget(event);
        }

        return START_STICKY;
    }

    private void saveSms(String sms_body) {
        Date now = new Date();
        long now_long = now.getTime();

        ContentValues values = new ContentValues();
        values.put(SmsTable.COLUMN_DATE, now_long);
        values.put(SmsTable.COLUMN_TEXT, sms_body);

        getContentResolver().insert(SmsContentProvider.CONTENT_URI, values);
    }

    private SmsData processSms(String sms_body) {
        Pattern pattern = Pattern.compile(getString(R.string.sms_pattern));
        Matcher matcher = pattern.matcher(sms_body);
        if (matcher.matches()) {
            SmsData data = new SmsData();
            data.cardNumber = Integer.parseInt(matcher.group(1));
            data.datetime = matcher.group(2);
            data.amount = Float.parseFloat(matcher.group(4));
            if (matcher.group(3).equals(getString(R.string.credit))
                    || matcher.group(3).equals(getString(R.string.cash)))
                data.operationSign = true;
            else if (matcher.group(3).equals(getString(R.string.debit))
                    || matcher.group(3).equals(getString(R.string.cash_disbursement))
                    || matcher.group(3).equals(getString(R.string.online_bank))
                    || matcher.group(3).equals(getString(R.string.buy))) {
                data.operationSign = false;
                data.amount = -data.amount;
            }
            data.balance = Float.parseFloat(matcher.group(5));
            return data;
        }
        return null;
    }

    static class SmsData {
        int cardNumber;
        String datetime;
        boolean operationSign;
        float amount;
        float balance;
    }
}
