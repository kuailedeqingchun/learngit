package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.utils.StatusBarUtil;
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    private EditText registerusername;//用户名
    private EditText registerpasswd;//登录密码
    private EditText confirmPassword;//确认登录密码
    private EditText startpasswd;//启动密码
    private EditText confirmStartPassword;//确认启动密码
    private EditText yanzhegnma;//验证码
    private Button yanzhengmaSubmit;//获取验证码按钮
    private String PhoneId;//手机ID
    private String CheckCode;//验证码
    private String LoginPwd;//登录密码
    private String StartPwd;//启动密码
    private String lala = null;//自定义变量，用于输出错误信息
    int i =60;//获取验证码初始倒计时
    //消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                yanzhengmaSubmit.setText("重新发送(" + i + ")");
                yanzhengmaSubmit.setClickable(false);
            } else if (msg.what == -8) {
                yanzhengmaSubmit.setText("获取验证码");
                yanzhengmaSubmit.setClickable(true);
                i = 30;
            }else if(msg.what == 1){
                ToastUtil.show(getApplicationContext(),"设置新密码成功");
                Intent intent = new Intent(getApplication(),Login.class);
                startActivity(intent);
                finish();
            }else if(msg.what == 2){
                ToastUtil.show(getApplicationContext(),lala);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_forget_password);
        StatusBarUtil.setColor(this, 0x2b2941, 10);
        //初始化UI
        initUI();
    }

    private void initUI() {
        registerusername = (EditText) findViewById(R.id.register_username);
        registerpasswd = (EditText) findViewById(R.id.register_passwd);
        confirmPassword = (EditText)findViewById(R.id.ConfirmPassword);
        startpasswd = (EditText)findViewById(R.id.start_passwd);
        confirmStartPassword = (EditText)findViewById(R.id.ConfirmStartPassword);
        yanzhegnma = (EditText)findViewById(R.id.yanzhengma);
        yanzhengmaSubmit = (Button)findViewById(R.id.yanzhengma_submit);
        yanzhengmaSubmit.setOnClickListener(this);
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        switch (v.getId()){
            case R.id.yanzhengma_submit:
                yanzhengmaSubmit.setClickable(false);
                yanzhengmaSubmit.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET,"http://apis.juhe.cn/mobile/get", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ToastUtil.show(getApplicationContext(), "获取验证码成功");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.show(getApplicationContext(),"网络连接有误");
                        Log.e("TAG", error.getMessage(), error);
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("ID", registerusername.getText().toString().trim());
                        return map;
                    }
                };
                mQueue.add(jsonObjectRequest1);
                mQueue.start();
                break;
            case R.id.submit:
                if(!registerpasswd.getText().toString().equals(confirmPassword.getText().toString())) {
                    ToastUtil.show(getApplicationContext(), "俩次登录密码不匹配");
                }
                else {
                    if (!startpasswd.getText().toString().equals(confirmStartPassword.getText().toString())) {
                        ToastUtil.show(getApplicationContext(), "俩次登录密码不匹配");
                    }else{
                        //开启一个新线程，找回密码
                        Thread t=new Thread(new MyRunnable());
                        t.start();
                    }
                }
                break;
        }
    }
//忘记密码函数
    public String Register() {
        Log.i("lalala","运行啦");
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "ResetPassword";
        SoapObject request = new SoapObject(nameSpace, method);
        PhoneId = registerusername.getText().toString().trim();
        CheckCode = yanzhegnma.getText().toString().trim();
        LoginPwd = registerpasswd.getText().toString().trim();
        StartPwd = startpasswd.getText().toString().trim();

        request.addProperty("PhoneId", PhoneId);
        request.addProperty("CheckCode",CheckCode);
        request.addProperty("LoginPwd",LoginPwd);
        request.addProperty("StartPwd",StartPwd);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo="nihao";
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
                lala = Register();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala",lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
