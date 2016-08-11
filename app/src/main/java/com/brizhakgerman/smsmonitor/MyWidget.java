package com.brizhakgerman.smsmonitor;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             int appWidgetId, SmsData data) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        widgetView.setTextViewText(R.id.tvCardNumber, context.getString(R.string.card_type) + "*" + data.cardNumber);
        widgetView.setTextViewText(R.id.tvBalance, String.valueOf(data.balance));
        widgetView.setTextViewText(R.id.tvLastOperation, data.datetime + " " + data.amount);
        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }
}
