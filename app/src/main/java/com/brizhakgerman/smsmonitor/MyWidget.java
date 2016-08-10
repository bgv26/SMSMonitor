package com.brizhakgerman.smsmonitor;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

public class MyWidget extends AppWidgetProvider {
    private final static String LOG_TAG = "MyLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
//        SharedPreferences sp = context.getSharedPreferences("widget_pref", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("widget_id", )
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDelete " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             SharedPreferences sp, int appWidgetId, SmsData data) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        widgetView.setTextViewText(R.id.tvCardNumber, "VISA" + data.cardNumber);
        widgetView.setTextViewText(R.id.tvBalance, String.valueOf(data.balance));
        widgetView.setTextViewText(R.id.tvLastOperation, data.date + " " + data.time + " " + data.amount);
        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }
}
