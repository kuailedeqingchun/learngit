package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
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
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {


    private EditText register_username;//用户名
    private EditText register_passwd;//密码
    private String lala = null;//自定义变量，用于打出错误信息
    private String ID;
    private String usertype;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                Intent intent = new Intent(getApplication(),Home.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
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
        setContentView(R.layout.content_login);
        //初始化UI
        initUI();
    }

    private void initUI() {
        register_username = (EditText)findViewById(R.id.register_username);
        register_passwd = (EditText)findViewById(R.id.register_passwd);
        Button login_submit = (Button)findViewById(R.id.login_submit);
        login_submit.setOnClickListener(this);
        Button register_submit = (Button)findViewById(R.id.register_submit);
        register_submit.setOnClickListener(this);
        Button forget_submit = (Button)findViewById(R.id.forget_submit);
        forget_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_submit:
                String username = register_username.getText().toString();
                String password = register_passwd.getText().toString();
                if(username.equals("")){
                    ToastUtil.show(getApplicationContext(),"手机号码不能为空");
                }else{
                    if(password.equals("")){
                        ToastUtil.show(getApplicationContext(),"密码不能为空");
                    }else{
                        Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                        t.start();
                    }

                }
                break;
            case R.id.register_submit:
                Intent intent1 = new Intent(this,Register.class);
                startActivity(intent1);
                break;
            case R.id.forget_submit:
                Intent intent2 = new Intent(this,ForgetPassword.class);
                startActivity(intent2);
                break;

        }

    }
    //登录函数
    public String[] Login() {
        Log.i("lalala","运行啦");
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "Login";
        ID = register_username.getText().toString().trim();
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", register_username.getText().toString().trim());
        request.addProperty("LoginPwd",register_passwd.getText().toString().trim());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String[] jieguo=new String[100];
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo[0] = bbb.getString("errmsg");
            jieguo[1] = bbb.getString("usertype");
            usertype = jieguo[1];
            Log.i("lalala",jieguo[0]);
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo[1]);
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
                lala = Login()[0];
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
