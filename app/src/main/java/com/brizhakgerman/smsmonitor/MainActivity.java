package com.brizhakgerman.smsmonitor;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    static Set<Long> selectedItemIds = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list);
        fillData();
        final ListView listView = getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                checkItem(view, l);
                setActionBarTitle();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (selectedItemIds.size() > 0) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    for (long selectedItem : selectedItemIds) {
                        Uri messageUri = Uri.parse(SmsContentProvider.CONTENT_URI + "/" + selectedItem);
                        int res = getContentResolver().delete(messageUri, null, null);
                        if (res != 0)
                            getLoaderManager().initLoader(0, null, this);

                    }
                    selectedItemIds.clear();
                    setActionBarTitle();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemDelete = menu.findItem(R.id.action_delete);
        itemDelete.setVisible(selectedItemIds.size() != 0);
        return super.onPrepareOptionsMenu(menu);
    }

    private void fillData() {
        String[] from = new String[]{SmsTable.COLUMN_DATE, SmsTable.COLUMN_TEXT};
        int[] to = new int[]{R.id.smsDate, R.id.smsMessage};

        getLoaderManager().initLoader(0, null, this);
        adapter = new CheckableSimpleCursorAdapter(this, R.layout.sms_row, null, from,
                to, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndex(SmsTable.COLUMN_DATE)) {
                    Date d = new Date(cursor.getLong(columnIndex));
                    String formatted = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()).format(d);
                    ((TextView) view).setText(formatted);
                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Uri messageUri = Uri.parse(SmsContentProvider.CONTENT_URI + "/" + id);
        String[] projection = {SmsTable.COLUMN_DATE, SmsTable.COLUMN_TEXT};
        Cursor cursor = getContentResolver().query(messageUri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String message = cursor.getString(cursor.getColumnIndexOrThrow(SmsTable.COLUMN_TEXT));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message)
                    .setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();

            cursor.close();
        } else {
            Toast.makeText(this, "Can't extract SMS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {SmsTable.COLUMN_ID, SmsTable.COLUMN_DATE, SmsTable.COLUMN_TEXT};
        return new CursorLoader(this, SmsContentProvider.CONTENT_URI,
                projection, null, null, "-" + SmsTable.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void checkItem(View view, long id) {
        ImageView imgCheck = (ImageView) view.findViewById(R.id.img_check);

        if (!selectedItemIds.contains(id)) {
            selectedItemIds.add(id);
            imgCheck.setVisibility(View.VISIBLE);
        } else {
            selectedItemIds.remove(id);
            imgCheck.setVisibility(View.GONE);
        }
    }

    private void setActionBarTitle() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            int totalSelected = selectedItemIds.size();
            String title;
            if (totalSelected != 0) {
                title = String.valueOf(totalSelected);
            } else {
                title = getString(R.string.app_name);
            }
            actionBar.setTitle(title);
            invalidateOptionsMenu();
//            Toast.makeText(getApplicationContext(), String.valueOf(selectedItemIds), Toast.LENGTH_LONG).show();
        }
    }
}
