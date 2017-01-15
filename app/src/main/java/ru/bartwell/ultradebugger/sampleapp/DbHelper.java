package ru.bartwell.ultradebugger.sampleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BArtWell on 07.01.2017.
 */

class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "test_db";
    private static final String TABLE_NAME = "table1";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_NAME + " text" +
                ");");
        for (int i = 0; i < 15; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, i);
            contentValues.put(COLUMN_NAME, COLUMN_NAME + i);
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}