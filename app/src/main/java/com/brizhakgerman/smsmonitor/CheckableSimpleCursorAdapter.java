package com.brizhakgerman.smsmonitor;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

class CheckableSimpleCursorAdapter extends SimpleCursorAdapter {


    CheckableSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        ImageView imgCheck = (ImageView) view.findViewById(R.id.img_check);
        if (MainActivity.selectedItemIds.contains(cursor.getLong(cursor.getColumnIndex(SmsTable.COLUMN_ID))))
            imgCheck.setVisibility(View.VISIBLE);
        else
            imgCheck.setVisibility(View.GONE);
    }


}
