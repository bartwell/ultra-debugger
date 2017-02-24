package ru.bartwell.ultradebugger.module.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.utils.HttpUtils;
import ru.bartwell.ultradebugger.base.utils.CommonUtils;
import ru.bartwell.ultradebugger.base.html.ErrorPage;
import ru.bartwell.ultradebugger.base.html.Form;
import ru.bartwell.ultradebugger.base.html.Link;
import ru.bartwell.ultradebugger.base.html.LinksList;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 04.01.2017.
 */

public class Module extends BaseModule {
    private static final String PARAMETER_DATABASE = "db";
    private static final String PARAMETER_TABLE = "table";
    private static final String PARAMETER_EDIT = "edit";
    private static final String PARAMETER_SAVE = "save";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_FIELD = "field";
    private static final String COLUMN_ROW_ID = "rowid";

    public Module(@NonNull Context context, @NonNull String moduleId) {
        super(context, moduleId);
    }

    @NonNull
    @Override
    public String getName() {
        return getString(R.string.sqlite_name);
    }

    @NonNull
    @Override
    public String getDescription() {
        return getString(R.string.sqlite_description);
    }

    @NonNull
    @Override
    public HttpResponse handle(@NonNull HttpRequest request) {
        Page page;
        String database = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_DATABASE);
        String table = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_TABLE);
        if (database == null) {
            page = showDatabasesList();
        } else {
            if (table == null) {
                page = showTablesList(database);
            } else {
                String edit = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_EDIT);
                if (edit == null) {
                    String delete = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_DELETE);
                    if (!TextUtils.isEmpty(delete) && CommonUtils.isNumber(delete)) {
                        deleteItem(database, table, delete);
                    }
                    String save = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_SAVE);
                    if (save != null) {
                        saveItem(database, table, save, request);
                    }
                    page = showItemsList(database, table);
                } else {
                    page = showForm(database, table, edit);
                }
            }
        }
        page.setTitle(getName());
        return new HttpResponse(page.toHtml());
    }

    @NonNull
    private Page showForm(@NonNull String database, @NonNull String tableName, @Nullable String id) {
        try {
            boolean isEdit = CommonUtils.isNumber(id);
            SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
            Cursor cursor;
            if (isEdit) {
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableName + " WHERE " + COLUMN_ROW_ID + "=?", new String[]{id});
            } else {
                cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            }
            Page page = new Page();
            page.addNavigationLink("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + tableName, "Back to items");
            if (cursor.moveToFirst()) {
                Form form = new Form();
                form.setAction("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + tableName);
                form.addHidden(PARAMETER_SAVE, id);
                String[] columnNames = cursor.getColumnNames();
                for (String columnName : columnNames) {
                    String value = "";
                    if (isEdit) {
                        value = cursor.getString(cursor.getColumnIndex(columnName));
                    }
                    form.addInputText(columnName, PARAMETER_FIELD + "[" + columnName + "]", value);
                }
                form.addSubmit("Save");
                page.setSingleContentPart(form);
            }
            cursor.close();
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't show form: " + e.getMessage(), true);
        }
    }

    @NonNull
    private Page showItemsList(String database, String tableName) {
        try {
            SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + COLUMN_ROW_ID + ",* FROM " + tableName, null);
            Table table = new Table();
            if (cursor.moveToFirst()) {
                int y = 0;
                do {
                    if (y == 0) {
                        String[] columnNames = cursor.getColumnNames();
                        for (int x = 1; x < columnNames.length; x++) {
                            table.add(x - 1, 0, new RawContentPart(columnNames[x]));
                        }
                        y++;
                    }
                    int columnCount = cursor.getColumnCount();
                    for (int x = 1; x < columnCount; x++) {
                        table.add(x - 1, y, new RawContentPart(cursor.getString(x)));
                    }
                    table.add(columnCount - 1, y,
                            new Link("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + tableName + "&" + PARAMETER_EDIT + "="
                                    + cursor.getString(0), "Edit"));
                    table.add(columnCount, y,
                            new Link("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + tableName + "&" + PARAMETER_DELETE + "="
                                    + cursor.getString(0), "Delete"));
                    y++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            Page page = new Page();
            page.addNavigationLink("?", "Databases list");
            page.addNavigationLink("?" + PARAMETER_DATABASE + "=" + database, "Tables list");
            page.addNavigationLink("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + tableName + "&" + PARAMETER_EDIT + "=new", "Insert row");
            page.setSingleContentPart(table);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't read items: " + e.getMessage(), true);
        }
    }

    @NonNull
    private Page showTablesList(String database) {
        try {
            LinksList linksList = new LinksList();
            SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null);
            if (cursor.moveToFirst()) {
                do {
                    linksList.add("?" + PARAMETER_DATABASE + "=" + database + "&" + PARAMETER_TABLE + "=" + cursor.getString(0), cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            Page page = new Page();
            page.setSingleContentPart(linksList);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't get tables list: " + e.getMessage(), true);
        }
    }

    @NonNull
    private Page showDatabasesList() {
        String[] databases = getContext().databaseList();
        if (databases.length > 0) {
            LinksList linksList = new LinksList();
            for (String database : databases) {
                linksList.add("?" + PARAMETER_DATABASE + "=" + database, database);
            }
            Page page = new Page();
            page.setSingleContentPart(linksList);
            return page;
        } else {
            return new ErrorPage("No databases in this application", true);
        }
    }

    private void deleteItem(String database, String table, String id) {
        try {
            SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
            sqLiteDatabase.delete(table, COLUMN_ROW_ID + "=?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveItem(@NonNull String database, @NonNull String table, @NonNull String id, HttpRequest request) {
        try {
            Map<String, String> mapFromParameters = HttpUtils.getMapFromParameters(request.getParameters(), PARAMETER_FIELD);
            SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
            ContentValues contentValues = new ContentValues();
            for (Map.Entry<String, String> entry : mapFromParameters.entrySet()) {
                if (!TextUtils.isEmpty(entry.getValue())) {
                    contentValues.put(entry.getKey(), entry.getValue());
                }
            }
            if (contentValues.size() > 0) {
                if (CommonUtils.isNumber(id)) {
                    sqLiteDatabase.update(table, contentValues, COLUMN_ROW_ID + "=?", new String[]{id});
                } else {
                    sqLiteDatabase.insert(table, null, contentValues);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
