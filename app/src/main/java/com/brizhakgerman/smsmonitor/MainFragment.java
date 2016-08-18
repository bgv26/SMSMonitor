package com.brizhakgerman.smsmonitor;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MENU_DELETE = 1;

    private SimpleCursorAdapter adapter;
    private long selectedItemId = 0;
    private Context context;
    static ArrayList<Long> selectedItems = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView imgCheck = (ImageView) view.findViewById(R.id.img_check);
                if (imgCheck.getVisibility() == View.GONE) {
                    selectedItems.add(l);
                    imgCheck.setVisibility(View.VISIBLE);
                } else {
                    selectedItems.remove(l);
                    imgCheck.setVisibility(View.GONE);
                }
                ActionBar actionBar = getActivity().getActionBar();
                if (actionBar != null) {
                    getActivity().invalidateOptionsMenu();
                    int totalSelected = selectedItems.size();
                    String title;
                    if (totalSelected != 0)
                        title = String.valueOf(totalSelected);
                    else
                        title = getString(R.string.app_name);
                        actionBar.setTitle(title);
                }
                return false;
            }
        });
        registerForContextMenu(listView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        fillData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedItemId = info.id;

        menu.setHeaderTitle(String.valueOf(selectedItemId));
        switch (v.getId()) {
            case android.R.id.list:
                menu.add(0, MENU_DELETE, 0, "Delete");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedItemId != 0) {
            switch (item.getItemId()) {
                case MENU_DELETE:
                    Uri messageUri = Uri.parse(SmsContentProvider.CONTENT_URI + "/" + selectedItemId);
                    int res = context.getContentResolver().delete(messageUri, null, null);
                    if (res != 0)
                        getLoaderManager().initLoader(0, null, this);
                    break;

            }
        }
        return super.onContextItemSelected(item);

    }

    private void fillData() {
        String[] from = new String[]{SmsTable.COLUMN_DATE, SmsTable.COLUMN_TEXT};
        int[] to = new int[]{R.id.smsDate, R.id.smsMessage};

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(context, R.layout.sms_row, null, from,
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
        Cursor cursor = context.getContentResolver().query(messageUri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String message = cursor.getString(cursor.getColumnIndexOrThrow(SmsTable.COLUMN_TEXT));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();

            cursor.close();
        } else {
            Toast.makeText(context, "Can't extract SMS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {SmsTable.COLUMN_ID, SmsTable.COLUMN_DATE, SmsTable.COLUMN_TEXT};
        return new CursorLoader(context, SmsContentProvider.CONTENT_URI,
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
}
