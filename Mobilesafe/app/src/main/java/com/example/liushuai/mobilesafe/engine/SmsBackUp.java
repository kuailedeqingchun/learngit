package com.example.liushuai.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liushuai on 2016/5/27.
 */
public class SmsBackUp {
    private static int index = 0;
    public static void backup(Context ctx,String path,Callback callback){
        FileOutputStream fos = null;
        Cursor cursor = null;
        try{
            File file = new File(path);
            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address","date","type","body"}, null, null, null );
            fos = new FileOutputStream(file);
            //序列化
            XmlSerializer newSerializer = Xml.newSerializer();
            newSerializer.setOutput(fos,"utf-8");
            newSerializer.startDocument("utf-8",true);
            newSerializer.startTag(null,"smss");

            if(callback!=null){
                callback.setMax(cursor.getCount());
            }
            //写入到xml中
            while(cursor.moveToNext()){
                newSerializer.startTag(null,"sms");

                newSerializer.startTag(null,"address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null, "address");

                newSerializer.startTag(null, "date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null, "date");

                newSerializer.startTag(null, "type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null, "type");

                newSerializer.startTag(null, "body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null, "body");

                newSerializer.endTag(null, "sms");

                index++;
                Thread.sleep(500);
                if(callback!=null){
                    callback.setProgress(index);
                }
            }
            newSerializer.endTag(null,"smss");
            newSerializer.endDocument();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try {
                if(cursor!=null && fos!=null){
                    cursor.close();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public interface Callback{
        public void setMax(int max);
        public void setProgress(int index);
    }
}
