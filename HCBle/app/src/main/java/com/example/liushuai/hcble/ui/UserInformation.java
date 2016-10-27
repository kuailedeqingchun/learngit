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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
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
import java.util.List;

public class UserInformation extends AppCompatActivity {
    private EditText personname;//姓名
    private EditText IDNumber;//身份证号
    private EditText connect;//联系方式
    private EditText birthday;//出生日期
    private EditText adress;//地址
    private TextView phonenumber;//电话号即ID
    private RadioGroup rg;//性别
    private Button submit;//提交按钮
    private String ID;//用户ID
    private String usertype;//用户类型
    private List<UsersInfo> mUsersInfo;
    private String lala;//自定义变量，用于输出错误信息
    private List<String> perList = new ArrayList<String>();
    private String type;//性别类型
    JSONArray arr = new JSONArray();//json数组
//消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                ToastUtil.show(getApplicationContext(), "个人信息修改成功");
            }else if(msg.what == 2){
                ToastUtil.show(getApplicationContext(), lala);
            }else if(msg.what ==3){
                for(int i = 0;i < mUsersInfo.size();i++){
                    if(phonenumber.getText().toString().equals(mUsersInfo.get(i).getUserID())){
                        personname.setText(mUsersInfo.get(i).getUserName());
                        birthday.setText(mUsersInfo.get(i).getBirthday());
                        connect.setText(mUsersInfo.get(i).getConnect());
                        IDNumber.setText(mUsersInfo.get(i).getIDNumber());
                        adress.setText(mUsersInfo.get(i).getAdress());
                        if(mUsersInfo.get(i).getSex().equals("m")){
                            rg.check(R.id.male);
                        }else{
                            rg.check(R.id.female);
                        }

                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_information);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        //初始化UI
        init();
        //开启一个新线程，获取自已以前上传的个人信息
        Thread t2=new Thread(new MyRunnable2());
        t2.start();
    }

    private void init() {
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        personname = (EditText)findViewById(R.id.personname);
        IDNumber = (EditText)findViewById(R.id.bumen);
        connect = (EditText)findViewById(R.id.connect);
        birthday = (EditText)findViewById(R.id.birthday);
        phonenumber = (TextView)findViewById(R.id.phonenumber);
        phonenumber.setText(ID);
        adress = (EditText)findViewById(R.id.adress);
        rg = (RadioGroup)findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                type = checkedId == R.id.male ? "male":"female";
            }
        });
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                t.start();
            }
        });
    }
//修改个人信息函数
    public String AddUser() {
        Log.i("lalala", "运行啦");
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "EditPersonMessage";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("Name",personname.getText().toString().trim());
        request.addProperty("Sex",type);
        request.addProperty("BirthDate", birthday.getText().toString().trim());
        request.addProperty("Contact", connect.getText().toString().trim());
        request.addProperty("IDNumber", IDNumber.getText().toString().trim());
        request.addProperty("Addr", adress.getText().toString().trim());

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
//获取个人信息
    public JSONArray Getper() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListUser";
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
            Log.i("aaa",aaa);
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

    public class MyRunnable2 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = Getper();
                mUsersInfo = new ArrayList<UsersInfo>();
                Log.i("lalala",""+"length"+arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        UsersInfo usersInfo = new UsersInfo();
                        usersInfo.setUserID(temp.getString("PhoneId"));
                        usersInfo.setUserName(temp.getString("userName"));
                        usersInfo.setAdress(temp.getString("homeAddress"));
                        usersInfo.setBirthday(temp.getString("birthDate"));
                        usersInfo.setConnect(temp.getString("contactInformation"));
                        usersInfo.setIDNumber(temp.getString("IDNumber"));
                        usersInfo.setSex(temp.getString("sex"));
                        perList.add(usersInfo.getUserName());
                        mUsersInfo.add(usersInfo);
                        // Log.i("lalala","siuze "+mEquipmentInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(3);
        }
    }

}
