package com.example.liushuai.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liushuai on 2016/5/15.
 */
public class SpUtil {
    private static SharedPreferences sp;
    //写入boolean至sp中
    public static void putBoolean(Context ctx,String key,boolean value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    //读取boolean从sp中
    public static boolean getBoolean(Context ctx,String key,boolean defValue){
        //(读取节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    //写入密码变量至sp中
    public static void putString(Context ctx,String key,String value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }
    //读取密码从sp中
    public static String getString(Context ctx,String key,String defValue){
        //(读取节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }
    //删除节点
    public static void remove(Context ctx, String key) {
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    public static void putInt(Context ctx,String key,int value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }
    /**
     * 读取boolean标示从sp中
     * @param ctx	上下文环境
     * @param key	存储节点名称
     * @param defValue	没有此节点默认值
     * @return		默认值或者此节点读取到的结果
     */
    public static int getInt(Context ctx,String key,int defValue){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }


}
