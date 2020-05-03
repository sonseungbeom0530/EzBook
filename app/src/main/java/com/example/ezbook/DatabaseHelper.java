package com.example.ezbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user(id text primary key ,password text, name text, email text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
    }
    //inserting in database
    public boolean insert(String id, String password, String name, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("id",id);
        contentValues.put("password",password);
        contentValues.put("name",name);
        contentValues.put("email",email);
        long ins=db.insert("user",null,contentValues);
        if(ins==-1) return false;
        else return true;
    }

    //checking if id exists
    public boolean chkId(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user Where id=?",new String[]{id});
        if (cursor.getCount()>0) return false;
        else return true;
    }

    //checking the ID and password
    public boolean chkLogin(String id, String password ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user Where id=? and password=?",new String[]{id,password});
        if (cursor.getCount()>0) return true;
        else return false;

    }
}
