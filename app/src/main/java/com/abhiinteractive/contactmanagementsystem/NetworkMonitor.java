package com.abhiinteractive.contactmanagementsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;

/*Broadcast receiver to sync the data with MySQL whenever internet becomes available*/
public class NetworkMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ContactList.checkForNetwork(context)){
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

            Cursor cursor = dbHelper.readFromLocalDatabase(sqLiteDatabase);

            while (cursor.moveToNext()){
                int syncStatus = cursor.getInt(cursor.getColumnIndex("sync"));
                //If the sync was unsuccessful earlier due to no internet, then update the contact now
                if(syncStatus==DBContract.SYNC_FAILED){
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String phone = cursor.getString(cursor.getColumnIndex("phone"));
                    String website = cursor.getString(cursor.getColumnIndex("website"));
                    //An async task is being performed by the receiver
                    PendingResult pendingResult = goAsync();
                    //Execute the task to add contact to the server
                    AddContactTask addContactTask = new AddContactTask(context, pendingResult);
                    addContactTask.execute(name, email, phone, website);
                    //Set it's sync to successful now that it's synced
                    dbHelper.updateDatabase(name, email, phone, website, DBContract.SYNC_SUCCESS, sqLiteDatabase);
                    context.sendBroadcast(new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
                }
            }

            dbHelper.close();
        }
    }
}
