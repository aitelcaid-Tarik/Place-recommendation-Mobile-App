package com.example.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB_sqlite extends SQLiteOpenHelper {

    public static final String DBname = "data.db";


    public DB_sqlite(Context context) {
        super(context, DBname, null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table coordinates (name TEXT , description TEXT, latitude TEXT, longitude TEXT, rating TEXT) ");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS coordinates");
        //onCreate(db);

    }


    public boolean insertData(String name, String description, String latitude, String longitude , String rating) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        db.execSQL("create table IF NOT EXISTS coordinates (name TEXT , description TEXT, latitude TEXT,  longitude TEXT, rating TEXT) ");

        content.put("name", name);
        content.put("description", description);
        content.put("latitude", latitude);
        content.put("longitude", longitude);
        content.put("rating", rating);

        long result = db.insert("coordinates", null, content);

        if (result == -1) return false;

        return true;
    }


    public void drop() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("drop table IF EXISTS coordinates ");


    }


    public ArrayList<Coordinates> getAll() {

        ArrayList<Coordinates> tab = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from coordinates ", null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {

            String name = res.getString(0);
            String description = res.getString(1);
            String lat = res.getString(2);
            String lon = res.getString(3);
            String rat = res.getString(4);

            tab.add(new Coordinates(name, description, lat, lon, rat));

            res.moveToNext();
        }
        return tab;
    }

}
