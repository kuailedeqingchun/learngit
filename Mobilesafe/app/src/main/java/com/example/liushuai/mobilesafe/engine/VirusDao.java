package com.example.liushuai.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuai on 2016/6/6.
 */
public class VirusDao {
    public static String path = "data/data/com.example.liushuai.mobilesafe/files/antivirus.db";
    //查询数据库对应的md5码
    public static List<String> getVirusList(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<String>();
        while(cursor.moveToNext()){
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
