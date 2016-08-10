package com.brizhakgerman.smsmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Сбербанк")
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(R.mipmap.ic_launcher, notification);
    }

    private void updateWidget(SmsData data) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int ids[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyWidget.class));
        MyWidget.updateWidget(this, appWidgetManager, null, ids[0], data);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms_body = intent.getExtras().getString("sms_body");
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
        Pattern pattern = Pattern.compile(getResources().getText(R.string.sms_pattern).toString());
//        if (pattern.matcher(sms_body).matches()) {
        Matcher matcher = pattern.matcher(sms_body);
        if (matcher.matches()) {
            SmsData data = new SmsData();
            data.cardNumber = Integer.parseInt(matcher.group(1));
            data.date = matcher.group(2);
            data.time = matcher.group(3);
            if (matcher.group(4).equals("зачисление"))
                data.amount = Float.parseFloat(matcher.group(5));
            else if (matcher.group(4).equals("списание"))
                data.amount = -Float.parseFloat(matcher.group(5));
            data.balance = Float.parseFloat(matcher.group(6));
            return data;
        }
        return null;
    }
}
