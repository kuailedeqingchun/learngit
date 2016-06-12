package com.example.liushuai.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liushuai.mobilesafe.db.BlackNumberOpenHelper;
import com.example.liushuai.mobilesafe.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuai on 2016/5/26.
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;
    private BlackNumberDao(Context context){
        //创建数据库
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    //声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;
    //提供一个静态方法
    public static BlackNumberDao getInstance(Context context){
        if(blackNumberDao == null){
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }
    //增加
    public void insert(String phone,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);

        db.close();
    }
    //删除
    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        db.delete("blacknumber", "phone = ?", new String[]{phone});

        db.close();
    }
    //更新
    public void update(String phone,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);

        db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});

        db.close();
    }
    //查询
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index+""});

        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }
    //获取数据库中的条目
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }
    //获取电话号码的拦截模式
    public int getMode(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null,null);
        if(cursor.moveToNext()){
            mode = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return mode;
    }
}
