package com.smartflyer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "smartflyer";

    // State table name
    private static final String TABLE_ACCOUNTS = "account";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_STATE = "state";
    private static final String KEY_SEARCHES = "searches";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_STATE + " TEXT,"
                + KEY_SEARCHES + " TEXT)";
        db.execSQL(CREATE_RECIPE_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        // Create tables again
        onCreate(db);
    }

    public HashMap<String, String> getLoggedInUser(){
        Log.d(TAG + ":getLoggedInUser","Reached");

        //Check User Entry if exists
        HashMap<String, String> user = null;
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS + " where state='" + Config.LOGIN_STATE+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            user = new HashMap<String, String>();
            Log.d(TAG + ":name",cursor.getString(1));
            Log.d(TAG + ":email",cursor.getString(2));
            Log.d(TAG + ":state",cursor.getString(3));
            user.put(KEY_NAME, cursor.getString(1));
            user.put(KEY_EMAIL, cursor.getString(2));
            Log.d(TAG + ":getLoggedInUser", "Fetching logged in user from Sqlite: " + user.toString());
            cursor.moveToNext();
            break;
        }
        cursor.close();
        db.close();
        // return user
        return user;

    }

    public boolean isUserPresent(String email){
        Log.d(TAG + ":isUserPresent", "User Email: " + email);
        //Check User Entry if exists
        boolean result = false;
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Log.d(TAG + ":isUserPresent", "Checking Email: " + cursor.getString(2));
            if(cursor.getString(2).equals(email)){
                Log.d(TAG + ":isUserPresent", "User Exists: " + email);
                result =  true;
            }
            cursor.moveToNext();
        }
        cursor.close();
        dbRead.close();
        return result;
    }

    /**
     * Update user login state
     */
    public void updateState(String e, String state) {
        String email = "'"+e+"'";

        Log.d(TAG + ":updateState", "User Email: " + email);

        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATE, state);
        String[] args = new String[]{email};
        dbWrite.update(TABLE_ACCOUNTS, values, "email="+email, null);
        Log.d(TAG, "State Updated for : " + email + "with " + state);
        dbWrite.close();
    }

    /**
     * Update user logout state
     */
    public void logoutUser(String email) {
        Log.d(TAG + ":logoutUser", "User Email: " + email);
        updateState(email,Config.LOGOUT_STATE);
    }

    /**
     * Add User to table
     */
    public void loginUser(String name, String email) {
        Log.d(TAG + ":loginUser", "User Email: " + email);
        if(isUserPresent(email)){
            updateState(email,Config.LOGIN_STATE);
            Log.d(TAG, "User exists, state updated: " + email);
        }else{
            SQLiteDatabase dbWrite = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name);
            values.put(KEY_EMAIL, email);
            values.put(KEY_STATE, Config.LOGIN_STATE);

            // Inserting Row
            long id = dbWrite.insert(TABLE_ACCOUNTS, null, values);
            dbWrite.close(); // Closing database connection
            Log.d(TAG, "New User inserted into sqlite: " + email);
        }
    }

    /**
     * Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_ACCOUNTS, null, null);
        db.close();
        Log.d(TAG, "Deleted all users info from sqlite");
    }
}