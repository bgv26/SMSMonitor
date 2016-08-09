package com.brizhakgerman.smsmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.IBinder;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsService extends Service {
    private class SmsData {
        public int hh;
        public int mm;
        public String description;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Регбол")
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(R.mipmap.ic_launcher, notification);
    }

    private void updateWidget(String text) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int ids[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyWidget.class));
        MyWidget.updateWidget(this, appWidgetManager, null, ids[0], text);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms_body = intent.getExtras().getString("sms_body");
//        showNotification(sms_body);
        updateWidget(sms_body);
        saveSms(sms_body);

        SmsData event = processSms(sms_body);
        if (event != null) {
            addEvent(event.hh, event.mm, event.description);
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
        Pattern pattern = Pattern.compile("Регбол! Сегодня в (\\d+)-(\\d+). (.+)");
        if (pattern.matcher(sms_body).matches()) {
            Matcher matcher = pattern.matcher(sms_body);
            matcher.find();
            SmsData data = new SmsData();
            data.hh = Integer.parseInt(matcher.group(1));
            data.mm = Integer.parseInt(matcher.group(2));
            data.description = matcher.group(3);
            return data;
        }
        return null;
    }

    private void addEvent(int hh, int mm, String description) {
        long calId = 1;
        long startMillis = 0;
        long endMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.HOUR_OF_DAY, hh);
        beginTime.set(Calendar.MINUTE, mm);
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, hh + 2);
        endTime.set(Calendar.MINUTE, mm);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, "Rugball");
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calId);
        values.put(Events.EVENT_TIMEZONE, "Asia/Yekaterinburg");
        cr.insert(Events.CONTENT_URI, values);
    }
}
