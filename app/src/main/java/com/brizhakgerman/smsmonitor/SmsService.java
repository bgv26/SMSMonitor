package com.brizhakgerman.smsmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.Build;
import android.os.IBinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsService extends Service {

    private AppWidgetManager appWidgetManager;
    private SharedPreferences sharedPreference;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.credit_cards)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(R.mipmap.credit_cards, notification);
    }

    private void updateWidget(SmsData data) {
        appWidgetManager = AppWidgetManager.getInstance(this);
        sharedPreference = getSharedPreferences(SmsMonitorWidget.WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        int widgetID = findWidgetIDbyCardNumber(data.cardNumber);
        if (widgetID != 0) {
            editor.putString(SmsMonitorWidget.WIDGET_BALANCE_TEXT + widgetID,
                    data.balance + "р");
            editor.putString(SmsMonitorWidget.WIDGET_LAST_OPERATION_TEXT + widgetID,
                    data.datetime + " " + data.amount + "р");
            editor.putBoolean(SmsMonitorWidget.WIDGET_LAST_OPERATION_SIGN + widgetID,
                    data.operationSign);
            editor.apply();

            SmsMonitorWidget.updateWidget(this, appWidgetManager, sharedPreference, widgetID);
        }
    }

    private int findWidgetIDbyCardNumber(String cardNumber) {
        int[] ids = appWidgetManager.getAppWidgetIds(
                new ComponentName(getPackageName(), SmsMonitorWidget.class.getCanonicalName()));
        for (int id : ids) {
            String curCardNumber = sharedPreference.getString(SmsMonitorWidget.WIDGET_CARD_NUMBER_TEXT + id, "0000");
            if (curCardNumber.equals(cardNumber))
                return id;
        }
        return 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms_body = intent.getExtras().getString(SmsMonitor.SMS_BODY);
        if (Build.VERSION.SDK_INT < 19) {
            showNotification(sms_body);
        }

        SmsData event = processSms(sms_body);
        if (event != null) {
            updateWidget(event);
        }

        saveSms(sms_body, event);

        return START_STICKY;
    }

    private void saveSms(String sms_body, SmsData data) {
        ContentValues values = new ContentValues();
        values.put(SmsTable.COLUMN_DATE, (new SimpleDate()).toLong());
        values.put(SmsTable.COLUMN_TEXT, sms_body);

        if (data != null) {
            values.put(SmsTable.COLUMN_OPERATION_DATE, (new SimpleDate(data.datetime)).toLong());
            values.put(SmsTable.COLUMN_CARD_NUMBER, data.cardNumber);
        }

        getContentResolver().insert(SmsContentProvider.CONTENT_URI, values);
    }

    private SmsData processSms(String sms_body) {
        Pattern pattern = Pattern.compile(getString(R.string.sms_pattern));
        Matcher matcher = pattern.matcher(sms_body);
        if (matcher.matches()) {
            SmsData data = new SmsData();
            data.cardNumber = matcher.group(1);
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
        String cardNumber;
        String datetime;
        boolean operationSign;
        float amount;
        float balance;
    }
}
