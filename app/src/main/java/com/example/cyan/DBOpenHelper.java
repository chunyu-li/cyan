package com.example.cyan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cyan.object.Request;
import com.example.cyan.object.User;

import java.util.ArrayList;

/**
 * @author Chunyu Li
 * @File: DBOpenHelper.java
 * @Package com.example.cyan
 * @date 12/12/20 8:33 PM
 * @Description: Since I found this app needs to update date continuously, I thought local cache
 * was useless and abandoned it
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public DBOpenHelper(Context context) {
        super(context, "cyan_db", null, 4);
        db = getReadableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(userId INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,password VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS requests(requestId INTEGER PRIMARY KEY AUTOINCREMENT, requesterId VARCHAR,receiverId VARCHAR,requesterName VARCHAR,requesterAvatar VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS requests");
        onCreate(db);
    }

    public void insertUser(String username, String password) {
        db.execSQL("INSERT INTO users(username,password) VALUES (?,?)", new Object[]{username, password});
    }

    public void insertRequest(String requesterId, String receiverId, String requesterName, String requesterAvatar) {
        db.execSQL("INSERT INTO requests(requesterId, receiverId, requesterName, requesterAvatar) VALUES (?,?,?,?)", new Object[]{requesterId, receiverId, requesterName, requesterAvatar});
    }

    //get the data of users table
    public ArrayList<User> getUserData() {
        ArrayList<User> list = new ArrayList<>();
        Cursor cursor = db.query("users", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            list.add(new User(username, password, "123",""));
        }
        return list;
    }

    public void deleteRequestData() {
        Cursor cursor = db.query("requests", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String requesterId = cursor.getString(cursor.getColumnIndex("requesterId"));
            String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
            db.execSQL("DELETE FROM requests WHERE requesterId= ? AND receiverId = ?", new Object[]{requesterId, receiverId});
        }
    }

    public void deleteRequest(String requesterId, String receiverId) {
        db.execSQL("DELETE FROM requests WHERE requesterId= ? AND receiverId = ?", new Object[]{requesterId, receiverId});
    }

    public ArrayList<Request> getRequestData() {
        ArrayList<Request> list = new ArrayList<>();
        Cursor cursor = db.query("requests", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String requesterId = cursor.getString(cursor.getColumnIndex("requesterId"));
            String receiverId = cursor.getString(cursor.getColumnIndex("receiverId"));
            String requesterName = cursor.getString(cursor.getColumnIndex("requesterName"));
            String requesterAvatar = cursor.getString(cursor.getColumnIndex("requesterAvatar"));
            list.add(new Request(requesterId, receiverId, requesterName, requesterAvatar));
        }
        return list;
    }
}
