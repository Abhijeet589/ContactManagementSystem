package com.abhiinteractive.contactmanagementsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;
    ArrayList<Contact> contactList = new ArrayList<>();
    FloatingActionButton add;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //Setup the recycler view and adapter
        recyclerView = findViewById(R.id.contacts_recycler_view);
        contactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(this, contactList);
        recyclerView.setAdapter(contactsRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add = findViewById(R.id.add_fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to the add contact screen
                startActivity(new Intent(ContactList.this, AddContact.class));
            }
        });

        //To load the contact list on opening the app
        readFromLocalStorage();

        //Receiver to sync SQLite with server when internet becomes available
        broadcastReceiver = new NetworkMonitor();
    }

    //To read from the SQLite database and populate the recyclerview
    private void readFromLocalStorage() {
        contactList.clear();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String website = cursor.getString(cursor.getColumnIndex("website"));
            int sync = cursor.getInt(cursor.getColumnIndex("sync"));
            contactList.add(new Contact(name, email, phone, website, sync));
        }

        contactsRecyclerViewAdapter.notifyDataSetChanged();
    }

    //Static method to check if internet is available or not.
    public static boolean checkForNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        readFromLocalStorage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
