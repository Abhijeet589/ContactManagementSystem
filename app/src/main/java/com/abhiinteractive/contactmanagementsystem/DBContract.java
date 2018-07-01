package com.abhiinteractive.contactmanagementsystem;

public class DBContract {

    //Some constant int's to indicate the sync status between SQLite and server
    public static final int SYNC_SUCCESS = 0;
    public static final int SYNC_FAILED = 1;
    public static final int UPDATE_PENDING = 2;
    public static final int DELETE_PENDING = 3;

    //public static final String INTENT_FILTER_NAME = "com.abhiinteractive.contactmanagementsystem.syncintent";
    //Database constants
    public static final String DATABASE_NAME = "contactdb";
    public static final String TABLE_NAME = "contact_info";
}
