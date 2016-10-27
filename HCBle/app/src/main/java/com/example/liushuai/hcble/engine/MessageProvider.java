package com.example.liushuai.hcble.engine;

import android.os.Debug;
import android.os.Handler;

import com.example.liushuai.hcble.model.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuai on 2016/9/23.
 */
public class MessageProvider {
    final Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
// TODO Auto-generated method stub
// 在此处添加执行的代码
            handler.postDelayed(this, 50);// 50是延时时长
        }
    };

    //handler.postDelayed(runnable, 50);// 打开定时器，执行操作
   // handler.removeCallbacks(runnable);// 关闭定时器处理
    public static List<MessageInfo> getmessageInfo(){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageTitle("授权消息");
        messageInfo.setMessageBody("张三申请5次授权");
        messageInfo.setReadState(false);
        List<MessageInfo> mmessageInfo = new ArrayList<MessageInfo>();
        mmessageInfo.add(messageInfo);
        return mmessageInfo;
    }
}
