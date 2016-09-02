package com.brizhakgerman.smsmonitor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigWidgetActivity extends Activity implements View.OnClickListener {

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;
    private EditText etCardNumber;

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

        etCardNumber = (EditText) findViewById(R.id.etCardNumber);
        SharedPreferences sharedPreferences = getSharedPreferences(SmsMonitorWidget.WIDGET_PREF, MODE_PRIVATE);
        String cardNumber = sharedPreferences.getString(SmsMonitorWidget.WIDGET_CARD_NUMBER_TEXT + widgetID, "");
        if (!cardNumber.equals(""))
            etCardNumber.setText(cardNumber);
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String sCardNumber = etCardNumber.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(SmsMonitorWidget.WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SmsMonitorWidget.WIDGET_CARD_NUMBER_TEXT + widgetID, sCardNumber);
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        SmsService.SmsData data = new SmsService.SmsData();
        data.cardNumber = sCardNumber;
        SmsMonitorWidget.updateWidget(this, appWidgetManager, sharedPreferences, widgetID);

        setResult(RESULT_OK, resultValue);
        finish();
    }
}
