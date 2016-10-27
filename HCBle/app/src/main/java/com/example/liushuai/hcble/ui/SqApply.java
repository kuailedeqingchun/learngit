package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.UsersInfo;
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqApply extends AppCompatActivity {
    private TextView person;//姓名
    private Spinner spinner;//设备列表下拉框
    private EditText number;//申请授权次数
    private EditText sqreason;//授权原因
    private Button submit;//提交
    private Button back;//返回按钮
    //public static String result;
    private String ID;//用户ID
    private String usertype;
    private String lala;
    private String equId;//设备ID
    //申请人姓名,从服务器获取
    private String personname;
    //设备类型，从服务器获取
    private String equipmentType;
    //申请次数
    private String sqnumber;
    private ArrayAdapter<String> adapter1;
    private List<EquipmentInfo> mEquipmentInfo;
    private List<String> equList = new ArrayList<String>();
    //申请原因
    private String reason;
    JSONArray arr = new JSONArray();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 1:
                    ToastUtil.show(getApplicationContext(), "申请提交成功");
                    break;
                case 2:
                    ToastUtil.show(getApplicationContext(), lala);
                    break;
                case 3:
                    adapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item, equList);
                    //adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    spinner.setAdapter(adapter1);
                    spinner.setSelection(0, true);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sq_apply);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        initUI();
        Thread t=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
        t.start();
    }

    private void initUI() {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        person = (TextView)findViewById(R.id.person);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取设备类型的值
                for (int i = 0; i < mEquipmentInfo.size(); i++) {
                    //Log.i("bbb",i+mEquipmentInfo.get(i).getEquipmentName());
                    if(adapter1.getItem(position).equals(mEquipmentInfo.get(i).getEquipmentName())){
                        equId = mEquipmentInfo.get(i).getEquipmentID();
                        Log.i("bbb",equId+"lalal");
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        number = (EditText)findViewById(R.id.number);
        sqreason = (EditText)findViewById(R.id.sqreason);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initdata();
                Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                t.start();
            }
        });
    }

    private void initdata() {
        personname = person.getText().toString();
        sqnumber = number.getText().toString();
        reason = sqreason.getText().toString();
    }
    //申请授权函数
    public String AddUser() {
        Log.i("lalala",ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "Apply";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId",equId);
        request.addProperty("applyTimes",number.getText().toString());
        request.addProperty("reason",sqreason.getText().toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }

    public class MyRunnable implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                lala = AddUser();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//获取设备函数
    public JSONArray Getepu() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEqu";
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

    public class MyRunnable1 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = Getepu();
                mEquipmentInfo = new ArrayList<EquipmentInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        // Log.i("lalala",temp.toString());
                        //Log.i("lalala",temp.getString("equipmentCode"));
                        EquipmentInfo equipmentInfo = new EquipmentInfo();
                        equipmentInfo.setEquipmentCode(temp.getString("equipmentCode"));
                        Log.i("lalala", "EquipmentCode" + equipmentInfo.getEquipmentCode());
                        equipmentInfo.setEquipmentID(temp.getString("equId"));
                        equipmentInfo.setEquipmentName(temp.getString("equipmentName"));
                        equipmentInfo.setEquipmentType(temp.getString("equipmentType"));
                        equipmentInfo.setSsCustomer("李四");
                        equipmentInfo.setUseState(temp.getString("useState"));
                        equList.add(equipmentInfo.getEquipmentName());
                        mEquipmentInfo.add(equipmentInfo);
                        // Log.i("lalala","siuze "+mEquipmentInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("bbb","siuze "+mEquipmentInfo.size());
            handler.sendEmptyMessage(3);
        }
    }

}
