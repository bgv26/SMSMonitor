package com.brizhakgerman.smsmonitor;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

    final static String WIDGET_PREF = "widget_pref";
    final static String WIDGET_CARD_NUMBER_TEXT = "widget_card_number_text_";
    final static String WIDGET_BALANCE_TEXT = "widget_balance_text_";
    final static String WIDGET_LAST_OPERATION_TEXT = "widget_last_operation_text_";
    final static String WIDGET_LAST_OPERATION_SIGN = "widget_last_operation_sign_";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, sharedPreferences, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int id : appWidgetIds) {
            editor.remove(WIDGET_CARD_NUMBER_TEXT + id);
            editor.remove(WIDGET_BALANCE_TEXT + id);
            editor.remove(WIDGET_LAST_OPERATION_TEXT + id);
            editor.remove(WIDGET_LAST_OPERATION_SIGN + id);
        }
        editor.apply();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             SharedPreferences sharedPreferences, int appWidgetId) {

        String cardNumber = context.getString(R.string.card_type) + "***";
        int iCardNumber = sharedPreferences.getInt(WIDGET_CARD_NUMBER_TEXT + appWidgetId, 0);
        if (iCardNumber == 0)
            cardNumber += "0000";
        else
            cardNumber += iCardNumber;

        String balance = "Баланс: " + sharedPreferences.getString(WIDGET_BALANCE_TEXT + appWidgetId, "0р");

        String lastOperation = sharedPreferences.getString(WIDGET_LAST_OPERATION_TEXT + appWidgetId, "");

        boolean lastOperationSign = sharedPreferences.getBoolean(WIDGET_LAST_OPERATION_SIGN + appWidgetId, true);

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        widgetView.setTextViewText(R.id.tvCardNumber, cardNumber);
        widgetView.setTextViewText(R.id.tvBalance, balance);
        widgetView.setTextViewText(R.id.tvLastOperation, lastOperation);
        if (lastOperationSign)
            widgetView.setInt(R.id.tvLastOperation, "setTextColor", Color.BLUE);
        else
            widgetView.setInt(R.id.tvLastOperation, "setTextColor", Color.RED);
        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }
}
