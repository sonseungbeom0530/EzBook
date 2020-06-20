package com.example.ezbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataBaseHandler extends SQLiteOpenHelper {

    private String DBTAG="DBHelper";

    public DataBaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHandler(Context context){
        super(context,"ITEMS_DB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ITEMS_TABLE(Item_Id,Item_PID,Item_Name,Item_Price_Each,Item_Price,Item_Quantity)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addItem(Contact contact){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Item_Id",contact.getItem_Id());
        contentValues.put("Item_PID",contact.getItem_PID());
        contentValues.put("Item_Name",contact.getItem_Name());
        contentValues.put("Item_Price_Each",contact.getItem_Price_Each());
        contentValues.put("Item_Price",contact.getItem_Price());
        contentValues.put("Item_Quantity",contact.getItem_Quantity());

        long result=sqLiteDatabase.insert("ITEMS_TABLE",null,contentValues);
        if (result>0){
            Log.d(DBTAG,"Record inserted");
        }else {
            Log.d(DBTAG,"Record failed");
        }

    }
    public void deleteItem(Contact contact){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Item_Id",contact.getItem_Id());

        long result=sqLiteDatabase.delete("ITEMS_TABLE","Item_Id=?",new String[]{String.valueOf(contentValues)});
    }
    public Cursor getAllData(){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String[] columns=new String[]{"Item_Id","Item_PID","Item_Name","Item_Price_Each","Item_Price","Item_Quantity"};
        Cursor cursor=sqLiteDatabase.query("ITEMS_TABLE",columns,null,null,null,null,null);

        return cursor;
    }
    public void deleteAll(){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        sqLiteDatabase.delete("ITEMS_TABLE",null,null);
    }
}