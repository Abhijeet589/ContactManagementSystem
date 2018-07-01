package com.abhiinteractive.contactmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create the table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS contact_info( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " `name` TEXT NOT NULL ," +
                " `email` TEXT NOT NULL ," +
                " `phone` TEXT NOT NULL," +
                " `website` TEXT NOT NULL," +
                " `sync` INTEGER NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE if exists " + DBContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //Method to save the data to the local database
    public void saveToLocalDatabase(String name, String email, String phone, String website, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("website", website);
        contentValues.put("sync", syncStatus);
        sqLiteDatabase.insert(DBContract.TABLE_NAME, null, contentValues);
    }

    //Method to read the data from local database
    public Cursor readFromLocalDatabase(SQLiteDatabase sqLiteDatabase) {
        String columnNames[] = {"id", "name", "email", "phone", "website", "sync"};
        return (sqLiteDatabase.query(DBContract.TABLE_NAME, columnNames, null, null, null, null, null));
    }

    //Method to update the data
    public void updateDatabase(String name, String email, String phone, String website, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("website", website);
        contentValues.put("sync", syncStatus);
        String selection = "name" + " LIKE ?";
        String[] selection_args = {name};
        sqLiteDatabase.update(DBContract.TABLE_NAME, contentValues, selection, selection_args);
    }

    //Method to delete a contact
    public void deleteRow(String name, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(DBContract.TABLE_NAME, "name LIKE ?", new String[]{name});
    }

}
