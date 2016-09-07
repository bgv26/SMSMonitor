package com.brizhakgerman.smsmonitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class SmsMonitorWidget extends AppWidgetProvider {

    final static String WIDGET_PREF = "sms_monitor_widget_pref";
    final static String WIDGET_PAY_SYSTEM_INT = "widget_pay_system_int_";
    final static String WIDGET_CARD_TYPE_BOOL = "widget_card_type_bool_";
    final static String WIDGET_CARD_NUMBER_TEXT = "widget_card_number_text_";
    final static String WIDGET_CARD_CREDIT_INT = "widget_card_credit_int_";
    final static String WIDGET_BALANCE_TEXT = "widget_balance_text_";
    final static String WIDGET_LAST_OPERATION_TEXT = "widget_last_operation_text_";
    final static String WIDGET_LAST_OPERATION_SIGN = "widget_last_operation_sign_";

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
            editor.remove(WIDGET_PAY_SYSTEM_INT + id);
            editor.remove(WIDGET_CARD_TYPE_BOOL + id);
            editor.remove(WIDGET_CARD_NUMBER_TEXT + id);
            editor.remove(WIDGET_CARD_CREDIT_INT + id);
            editor.remove(WIDGET_BALANCE_TEXT + id);
            editor.remove(WIDGET_LAST_OPERATION_TEXT + id);
            editor.remove(WIDGET_LAST_OPERATION_SIGN + id);
        }
        editor.apply();
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             SharedPreferences sharedPreferences, int appWidgetId) {

        int paySystem = sharedPreferences.getInt(WIDGET_PAY_SYSTEM_INT + appWidgetId, 0);

        boolean isCredit = sharedPreferences.getBoolean(WIDGET_CARD_TYPE_BOOL + appWidgetId, false);
        String creditLimit = "";

        if (isCredit) {
            int credit = sharedPreferences.getInt(WIDGET_CARD_CREDIT_INT + appWidgetId, 0);
            if (credit != 0)
                creditLimit = "Лимит: " + credit + "p";
        }

        String cardNumber = sharedPreferences.getString(WIDGET_CARD_NUMBER_TEXT + appWidgetId, "0000");

        String balance = "Баланс: " + sharedPreferences.getString(WIDGET_BALANCE_TEXT + appWidgetId, "0р");

        String lastOperation = sharedPreferences.getString(WIDGET_LAST_OPERATION_TEXT + appWidgetId, "");

        boolean lastOperationSign = sharedPreferences.getBoolean(WIDGET_LAST_OPERATION_SIGN + appWidgetId, true);

        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);

        widgetView.setImageViewResource(R.id.ivPaySystem, getPaySystemIcon(paySystem));
        widgetView.setTextViewText(R.id.tvCardNumber, cardNumber);
        widgetView.setTextViewText(R.id.tvBalance, balance);
        widgetView.setTextViewText(R.id.tvLastOperation, lastOperation);
        if (!TextUtils.isEmpty(creditLimit))
            widgetView.setTextViewText(R.id.tvLimit, creditLimit);
        if (lastOperationSign)
            widgetView.setInt(R.id.tvLastOperation, "setTextColor", Color.BLUE);
        else
            widgetView.setInt(R.id.tvLastOperation, "setTextColor", Color.RED);

        Intent configIntent = new Intent(context, ConfigWidgetActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.ivWidgetSettings, configPendingIntent);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.widgetBody, mainPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, widgetView);
    }

    private static int getPaySystemIcon(int i) {
        switch (i) {
            case R.id.rbMasterCard:
                return R.mipmap.widget_mastercard;
            case R.id.rbMaestro:
                return R.mipmap.widget_maestro;
            default:
                return R.mipmap.widget_visa;
        }
    }
}
