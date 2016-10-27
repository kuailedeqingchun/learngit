package com.example.liushuai.hcble.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.MessageInfo;
import com.example.liushuai.hcble.ui.Home;
import com.example.liushuai.hcble.ui.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service {
    public MessageService() {
    }

    private List<MessageInfo> count;
    private MyBinder binder = new MyBinder();
    public static int count1;//消息数量
    public static List<MessageInfo> mMessageInfo;
    private String ID;//用户ID
    public static int environNums;//未读数量
    JSONArray arr = new JSONArray();//json数组

    private Runnable mRunnable;

    public class MyBinder extends Binder{
        public List getCount(){
            return mMessageInfo;
        }
    }
    //消息处理机制
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if(count1==0){
                        Home.count.setText("");
                        Home.count.setBackground(null);
                    }else{
                        Home.count.setText(""+count1);
                        Home.count.setBackground(getResources().getDrawable(R.drawable.tips_circle));
                    }
                    break;
            }
        };
    };
    @Override
    public IBinder onBind(Intent intent) {
        ID = intent.getStringExtra("ID");
        Log.i("lalala", ID);
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(){
            @Override
            public void run() {
                while(true){
                    arr = AddUser();
                    mMessageInfo = new ArrayList<MessageInfo>();
                    for (int i = 0; i < arr.length(); i++){
                        try {
                            JSONObject temp = (JSONObject) arr.get(i);
                            MessageInfo messageInfo = new MessageInfo();
                            String aaa = temp.toString();
                            //Log.i("lalala",aaa);
                            messageInfo.setMessageTitle(temp.getString("msgHeader"));
                            messageInfo.setMessageBody(temp.getString("msgBody"));
                            messageInfo.setMsgId(temp.getString("msgId"));
                            messageInfo.setKeyId(temp.getString("KeyId"));
                            messageInfo.setMsgType(temp.getString("msgType"));
                            mMessageInfo.add(messageInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //List<String> environmentList = new ArrayList<String>();
                    SharedPreferences preferDataList = getSharedPreferences("EnvironDataList2", MODE_PRIVATE);
                    //int environNums = preferDataList.getInt("EnvironNums", 0);
//                    for (int i = 0; i < environNums; i++)
//                    {
//                        String environItem = preferDataList.getString(""+i, null);
//                        environmentList.add(environItem);
//                    }
                    count1 = mMessageInfo.size();
                    SharedPreferences preferDataList1 = getSharedPreferences("EnvironDataList",MODE_PRIVATE);
                    environNums = preferDataList1.getInt("EnvironNums", 0);
                    for(int i=0;i<mMessageInfo.size();i++){
                        for(int j=1;j<=environNums;j++){
                            if (mMessageInfo.get(i).getMsgId().equals(preferDataList.getString("" + j, null))){
                                count1--;
                                break;
                            }
                        }
                    }
                    //count1 = mMessageInfo.size();
                    //count1-=Message.ydmessage.size();
                    Log.i("ccc",count1+"");
                    mHandler.sendEmptyMessage(1);
                }

            }
        }.start();
    }
    //获取消息函数
    public JSONArray AddUser() {
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListMsg";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        //String jieguo=null;
        JSONArray jieguo = new JSONArray();
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            Log.i("lalala",aaa);
            JSONObject bbb = new JSONObject(aaa);
            String ccc = bbb.getString("arr");
            Log.i("lalala",ccc);
            jieguo = new JSONArray(ccc);
            //jieguo = new JSONArray(bbb);
        } catch (Exception e) {
            //Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }
    public void loadArray(Context mContext) {
        SharedPreferences mSharedPreference1= getSharedPreferences("liushuai", Activity.MODE_PRIVATE);
        Message.ydmessage.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for(int i=0;i<size;i++) {
            Message.ydmessage.add(mSharedPreference1.getString("Status_" + i, null));

        }
    }

}
