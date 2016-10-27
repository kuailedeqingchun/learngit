package com.example.liushuai.hcble.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.liushuai.hcble.model.MessageInfo;
import com.example.liushuai.hcble.model.RightInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class ApplyService extends Service {

    private String ID;//用户ID
    public static String mresult = "0";//授权状态
    public static String usestate;//设备使用状态
    private MyBinder binder = new MyBinder();
    JSONArray arr = new JSONArray();
    public static List<RightInfo> mRightInfo;
    public ApplyService() {
    }

    public class MyBinder extends Binder {
        public List getCount(){
            return mRightInfo;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        ID = intent.getStringExtra("ID");
        Log.i("ddd","Id"+ID);
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
                    mRightInfo = new ArrayList<RightInfo>();
                    for (int i = 0; i < arr.length(); i++){
                        try {
                            JSONObject temp = (JSONObject) arr.get(i);
                            RightInfo rightInfo = new RightInfo();
                            String aaa = temp.toString();
                            Log.i("ddd",aaa);
                            rightInfo.setEquipmentID(temp.getString("equipmentID"));
                            rightInfo.setSqCode(temp.getString("authorizedId"));
                            rightInfo.setSqResult(temp.getString("authResult"));
                            rightInfo.setSqNumber(temp.getString("applyTimes"));
                            rightInfo.setUsestate(temp.getString("useState"));
                            //mresult = temp.getString("authResult");
                            mRightInfo.add(rightInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(mRightInfo.size()>=1) {
                        mresult = mRightInfo.get(mRightInfo.size() - 1).getSqResult();
                        usestate = mRightInfo.get(mRightInfo.size() - 1).getUsestate();
                        Log.i("ddd", mRightInfo.get(mRightInfo.size() - 1).getSqResult());
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }.start();
    }
    //获取授权信息
    public JSONArray AddUser() {
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquApply";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId", "");
        request.addProperty("usePhoneId", ID);
        request.addProperty("applyTime1", "");
        request.addProperty("applyTime2", "");

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
            Log.i("ddd", "+++"+aaa);
            JSONObject bbb = new JSONObject(aaa);
            String ccc = bbb.getString("arr");
            Log.i("ddd","---"+ccc);
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
}
