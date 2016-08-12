package com.brizhakgerman.smsmonitor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends Activity {

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.activity_config);
    }

    public void onClick(View view) {
        EditText etCardNumber = (EditText) findViewById(R.id.etCardNumber);
        String sCardNumber = etCardNumber.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(MyWidget.WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MyWidget.WIDGET_CARD_NUMBER_TEXT + widgetID, Integer.parseInt(sCardNumber));
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        SmsService.SmsData data = new SmsService.SmsData();
        data.cardNumber = Integer.parseInt(sCardNumber);
        MyWidget.updateWidget(this, appWidgetManager, sharedPreferences, widgetID);

        setResult(RESULT_OK, resultValue);
        finish();
    }
}
