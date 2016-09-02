package com.brizhakgerman.smsmonitor;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class FilterDialog extends DialogFragment implements View.OnClickListener {

    private MainActivity activity;
    private CheckBox chbPeriod, chbCardNumber, chbText;
    private LinearLayout llPeriod;
    private Spinner lvCardNumber;
    private EditText etDateStart, etDateEnd, etText;
    private SimpleDate dateStart, dateEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        getDialog().setTitle(R.string.dialog_filter_title);
        View dialog = inflater.inflate(R.layout.dialog_filter, container, false);

        ImageButton btnStartPeriod = (ImageButton) dialog.findViewById(R.id.btnDateStart);
        btnStartPeriod.setOnClickListener(this);

        etDateStart = (EditText) dialog.findViewById(R.id.dateStart);
        etDateEnd = (EditText) dialog.findViewById(R.id.dateEnd);

        ImageButton btnEndPeriod = (ImageButton) dialog.findViewById(R.id.btnDateEnd);
        btnEndPeriod.setOnClickListener(this);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        llPeriod = (LinearLayout) dialog.findViewById(R.id.llPeriod);

        chbPeriod = (CheckBox) dialog.findViewById(R.id.chbPeriod);
        chbPeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                llPeriod.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        lvCardNumber = (Spinner) dialog.findViewById(R.id.lvCardNumber);

        fillSpinner();

        chbCardNumber = (CheckBox) dialog.findViewById(R.id.chbCardNumber);
        chbCardNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                lvCardNumber.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        etText = (EditText) dialog.findViewById(R.id.etText);
        chbText = (CheckBox) dialog.findViewById(R.id.chbText);
        chbText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etText.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        return dialog;
    }

    @Override
    public void onClick(View view) {
        DatePickerDialog datePickerDialog;

        switch (view.getId()) {
            case R.id.btnOk:
                Bundle saveBundle = new Bundle();
                if (chbPeriod.isChecked()) {
                    String strDateStart = etDateStart.getText().toString();
                    String strDateEnd = etDateEnd.getText().toString();
                    if (!TextUtils.isEmpty(strDateStart)) {
                        if (dateStart == null) {
                            dateStart = new SimpleDate(strDateStart);
                        }
                        saveBundle.putLong("dateStart", dateStart.toLong());
                    }
                    if (!TextUtils.isEmpty(strDateEnd)) {
                        if (dateEnd == null) {
                            dateEnd = new SimpleDate(strDateEnd);
                        }
                        saveBundle.putLong("dateEnd", dateEnd.toLong());
                    }
                }
                if (chbCardNumber.isChecked()) {
                    saveBundle.putString("cardNumber", lvCardNumber.getSelectedItem().toString());
                }
                if (chbText.isChecked()) {
                    String text = etText.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        saveBundle.putString("text", text);
                    }
                }
                activity.setFilters(saveBundle, true);
                dismiss();
                break;
            case R.id.btnDateStart:
                if (dateStart == null) {
                    dateStart = setDateFromEditText(etDateStart);
                }
                datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                dateStart = new SimpleDate(year, month, day);
                                etDateStart.setText(dateStart.toString());
                            }
                        },
                        dateStart.year,
                        dateStart.month,
                        dateStart.day);
                datePickerDialog.setTitle(getString(R.string.dialog_filter_datepickerdialog_title));
                datePickerDialog.show();
                break;
            case R.id.btnDateEnd:
                if (dateEnd == null) {
                    dateEnd = setDateFromEditText(etDateEnd);
                }
                datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                dateEnd = new SimpleDate(year, month, day);
                                etDateEnd.setText(dateEnd.toString());
                            }
                        },
                        dateEnd.year,
                        dateEnd.month,
                        dateEnd.day);
                datePickerDialog.setTitle(getString(R.string.dialog_filter_datepickerdialog_title));
                datePickerDialog.show();
                break;
        }
    }

    private void fillSpinner() {
        SmsDatabaseHelper databaseHelper = new SmsDatabaseHelper(activity);
        ArrayList<String> cards = databaseHelper.getCards();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, cards);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lvCardNumber.setAdapter(adapter);
    }

    private SimpleDate setDateFromEditText(EditText editText) {
        String text = editText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
                return new SimpleDate(text);
        }
        return new SimpleDate();
    }
}
