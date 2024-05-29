package com.example.lostfoundapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostAndFound.db";
    private static final String TABLE_NAME = "adverts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TYPE";
    private static final String COL_3 = "NAME";
    private static final String COL_4 = "PHONE";
    private static final String COL_5 = "DESCRIPTION";
    private static final String COL_6 = "DATE";
    private static final String COL_7 = "LOCATION";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TYPE TEXT, NAME TEXT, PHONE TEXT, DESCRIPTION TEXT, DATE TEXT, LOCATION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAdvert(String type, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, type);
        contentValues.put(COL_3, name);
        contentValues.put(COL_4, phone);
        contentValues.put(COL_5, description);
        contentValues.put(COL_6, date);
        contentValues.put(COL_7, location);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public ArrayList<String> getAllAdverts() {
        ArrayList<String> advertsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while(res.moveToNext()) {
            StringBuilder advert = new StringBuilder();
            advert.append("Type: ").append(res.getString(1)).append("\n");
            advert.append("Name: ").append(res.getString(2)).append("\n");
            advert.append("Phone: ").append(res.getString(3)).append("\n");
            advert.append("Description: ").append(res.getString(4)).append("\n");
            advert.append("Date: ").append(res.getString(5)).append("\n");
            advert.append("Location: ").append(res.getString(6)).append("\n");
            advertsList.add(advert.toString());
        }
        res.close();
        return advertsList;
    }

    public boolean deleteAdvert(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }
}
