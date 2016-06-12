package com.example.liushuai.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liushuai on 2016/5/12.
 */
public class ToastUtil {
    public static void show(Context ctx,String msg) {
        Toast.makeText(ctx, msg, 0).show();
    }
}
