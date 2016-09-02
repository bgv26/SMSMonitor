package com.brizhakgerman.smsmonitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class SimpleDate {
    int day, month, year;
    private int hour, minute;

    SimpleDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        setDateFromCalendar(calendar);
    }

    SimpleDate(int year, int month, int day) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = 0;
        this.minute = 0;
    }

    SimpleDate(String dateString) {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        SimpleDateFormat formatDateFullYear = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        Date date;

        try {
            date = formatDateTime.parse(dateString);
        } catch (ParseException pe1) {
            try {
                date = formatDate.parse(dateString);
            } catch (ParseException pe2) {
                try {
                    date = formatDateFullYear.parse(dateString);
                } catch (ParseException pe3) {
                    throw new IllegalArgumentException();
                }
            }
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        setDateFromCalendar(calendar);
    }

    private void setDateFromCalendar(Calendar calendar) {
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public String toString() {
        return String.format(Locale.getDefault(), "%1$02d.%2$02d.%3$04d", day, month + 1, year);
    }

    long toLong() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month + 1, day, hour, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }
}
