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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class AddYonghu extends AppCompatActivity {
    private EditText phonenumber;//手机号码输入框
    private EditText dephonenumber;//确认手机号码输入框
    private RadioGroup rg;
    private Button submit;//提交按钮
    private String ID;//用户ID
    private String usertype;
    private String lala;//自定义变量，用于输出错误信息
    //消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                phonenumber.setText("");
                dephonenumber.setText("");
                ToastUtil.show(getApplicationContext(), "添加成功");
            }else if(msg.what == 2){
                ToastUtil.show(getApplicationContext(), "添加失败");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_yonghu);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        Log.i("guolai",ID);
        init();
    }

    private void init() {
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phonenumber = (EditText)findViewById(R.id.phonenumber);
        dephonenumber = (EditText)findViewById(R.id.dephonenumber);
//        rg = (RadioGroup)findViewById(R.id.rg);
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                String type = checkedId == R.id.putong ? "putong":"guanliyuan";
//            }
//        });
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phonenumber.getText().toString().equals(dephonenumber.getText().toString())) {
                    //新开启一个线程，提交添加的手机号
                    Thread t = new Thread(new MyRunnable());
                    t.start();
                }else{
                    ToastUtil.show(getApplicationContext(),"俩次手机号输入不一致");
                }
            }
        });
    }
    //添加用户函数
    public String AddUser() {
        Log.i("lalala",ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "AddUser";
        SoapObject request = new SoapObject(nameSpace, method);
        Log.i("tianjia",phonenumber.getText().toString());

        request.addProperty("PhoneId", ID);
        request.addProperty("userPhoneId",phonenumber.getText().toString().trim());

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

}
