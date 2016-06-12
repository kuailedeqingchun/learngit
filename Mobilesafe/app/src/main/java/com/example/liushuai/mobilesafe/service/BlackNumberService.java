package com.example.liushuai.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.example.liushuai.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;
import com.android.internal.telephony.*;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {
    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private MyContentObserver mContentObserver;
    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);
        //监听电话状态
        mTM = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(mInnerSmsReceiver!=null){
            unregisterReceiver(mInnerSmsReceiver);
        }
        //注销内容观察者
        if(mContentObserver!=null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }

        //取消对电话状态的监听
        if(mPhoneStateListener!=null){
            mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //挂断电话 	aidl文件中去了
//				mTM.endCall();
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String phone) {
        int mode = mDao.getMode(phone);
        if(mode == 2 || mode == 3){
            try {
                //1,获取ServiceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2,获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3,反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4,调用获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5,调用在aidl中隐藏的endCall方法
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //注册内容观察者，观察数据库
            mContentObserver = new MyContentObserver(new Handler(),phone);
            getContentResolver().registerContentObserver(
                    Uri.parse("content://call_log/calls"), true, mContentObserver);
        }
    }

    class MyContentObserver extends ContentObserver{
        private String phone;

        public MyContentObserver(Handler handler,String phone) {
            super(handler);
            this.phone=phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            //插入一条数据后再进行删除
            getContentResolver().delete(
                    Uri.parse("content://call_log/calls"),"number = ?", new String[]{phone});

            super.onChange(selfChange);
        }
    }

    class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //1,获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for(Object object : objects){
                SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                int mode = mDao.getMode(originatingAddress);
                if(mode == 1 || mode == 3){
                    abortBroadcast();
                }
            }
        }
    }

}
