package com.example.liushuai.hcble.engine;

import android.util.Log;

import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.YonghuList;
import com.example.liushuai.hcble.ui.Equiplist;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liushuai on 2016/9/24.
 */
//public class EquipmentProvider {
//    public static List<EquipmentInfo> getequipList(String ID){
//        Thread t=new Thread(Equiplist.MyRunnable());//这里比第一种创建线程对象多了个任务对象
//        t.start();
//        EquipmentInfo equipmentInfo = new EquipmentInfo();
//        equipmentInfo.setEquipmentCode("" + 100);
//        equipmentInfo.setEquipmentID("" + 1);
//        equipmentInfo.setEquipmentName("设备一");
//        equipmentInfo.setEquipmentType("通用型");
//        equipmentInfo.setSsCustomer("张三");
//        equipmentInfo.setUseState("未使用");
//        List<EquipmentInfo> mequipmentInfos = new ArrayList<EquipmentInfo>();
//        mequipmentInfos.add(equipmentInfo);
//        return mequipmentInfos;
//    }
//
//    public String AddUser(String ID) {
//        Log.i("lalala", ID);
//        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
//        String nameSpace = "http://tempuri.org/";
//        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
//        String method = "AddUser";
//        SoapObject request = new SoapObject(nameSpace, method);
//
//        request.addProperty("PhoneId", ID);
//        request.addProperty("userPhoneId", phonenumber.getText().toString().trim());
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        HttpTransportSE ht = new HttpTransportSE(url);
//        ht.debug = true;
//        envelope.bodyOut = request;
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(request);
//        //new MarshalDouble().register(envelope);
//        String jieguo=null;
//        try {
//            ht.call(nameSpace+method, envelope);
//            // 获取服务器响应返回的SOAP消息
//            Object result = (Object)envelope.getResponse();
//            String aaa = result.toString();
//            JSONObject bbb = new JSONObject(aaa);
//            jieguo = bbb.getString("errmsg");
//        } catch (Exception e) {
//            Log.i("lalala", "不成功" + jieguo);
//            e.printStackTrace();
//            String ee = e.getMessage();
//            Log.e("eee",ee);
//        }
//        return jieguo;
//}
//
//public class MyRunnable implements Runnable {
//    public void run() {
//        //你需要实现的代码
//        //String lala = null;
//        try {
//            lala = AddUser();
//            if(!lala.equals("")){
//                handler.sendEmptyMessage(2);
//            }else {
//                handler.sendEmptyMessage(1);
//            }
//            Log.i("lalala", lala);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//}
