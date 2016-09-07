package com.brizhakgerman.smsmonitor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class ConfigWidgetActivity extends Activity implements View.OnClickListener {

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;
    private EditText etCardNumber, etCreditSum;
    private CheckBox chbCredit;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SmsMonitorWidget.WIDGET_PREF, MODE_PRIVATE);
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

        RadioGroup rgCardSystem = (RadioGroup) findViewById(R.id.rgCardSystem);
        rgCardSystem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SmsMonitorWidget.WIDGET_PAY_SYSTEM_INT + widgetID, i);
                editor.apply();
            }
        });

        chbCredit = (CheckBox) findViewById(R.id.chbCredit);
        chbCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    int credit = sharedPreferences.getInt(SmsMonitorWidget.WIDGET_CARD_CREDIT_INT + widgetID, 0);
                    etCreditSum.setText(String.valueOf(credit));
                    etCreditSum.setVisibility(View.VISIBLE);
                }
            }
        });
        etCreditSum = (EditText) findViewById(R.id.etCreditSum);

        final String cardNumber = sharedPreferences.getString(SmsMonitorWidget.WIDGET_CARD_NUMBER_TEXT + widgetID, "");
        if (!cardNumber.equals(""))
            etCardNumber.setText(cardNumber);

        int paySystem = sharedPreferences.getInt(SmsMonitorWidget.WIDGET_PAY_SYSTEM_INT + widgetID, 0);
        if (paySystem != 0)
            setRadioGroup(paySystem);

        boolean isCredit = sharedPreferences.getBoolean(SmsMonitorWidget.WIDGET_CARD_TYPE_BOOL + widgetID, false);
        chbCredit.setChecked(isCredit);

        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String sCardNumber = etCardNumber.getText().toString();
        boolean isCredit = chbCredit.isChecked();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SmsMonitorWidget.WIDGET_CARD_NUMBER_TEXT + widgetID, sCardNumber);
        editor.putBoolean(SmsMonitorWidget.WIDGET_CARD_TYPE_BOOL + widgetID, isCredit);
        if (isCredit) {
            int creditLimit = Integer.parseInt(etCreditSum.getText().toString());
            editor.putInt(SmsMonitorWidget.WIDGET_CARD_CREDIT_INT + widgetID, creditLimit);
        }
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        SmsService.SmsData data = new SmsService.SmsData();
        data.cardNumber = sCardNumber;
        SmsMonitorWidget.updateWidget(this, appWidgetManager, sharedPreferences, widgetID);

        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void setRadioGroup(int id) {
        RadioButton btn = (RadioButton) findViewById(id);
        btn.setChecked(true);

    }
}
