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
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ShouQMessage extends AppCompatActivity {
    private Button confirm;//确认按钮
    private Button cancel;//取消按钮
    private String ID;
    private String usertype;
    private String lala;//自定义变量，用于输出错误信息
    private String msgID;//消息ID
    private String KeyId;//消息ID，把消息设为已读时用到
    private String msgBody;//消息体
    private EditText time;//时间
    private TextView xiaoxi;//消息体显示框
    //消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                ToastUtil.show(getApplicationContext(), "处理授权成功");
                confirm.setEnabled(false);
                cancel.setEnabled(false);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_shou_qmessage);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        msgID = bundle.getString("msgId");
        KeyId = bundle.getString("KeyId");
        msgBody = bundle.getString("msgBody");
        Log.i("lalala", ID);
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
        time = (EditText)findViewById(R.id.times);
        xiaoxi = (TextView)findViewById(R.id.xiaoxi);
        xiaoxi.setText(msgBody);
        confirm = (Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //授权通过告知服务器
                Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                t.start();
                //消息已读告知服务器
                Thread t1=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
                t1.start();
            }
        });
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //授权不通过告知服务器
                Thread t = new Thread(new MyRunnable2());//这里比第一种创建线程对象多了个任务对象
                t.start();
                //消息已读告知服务器
                Thread t1=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
                t1.start();
            }
        });
    }
//授权通过函数
    public String AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "AuthoritysEqu";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("authorizedId", KeyId);
        request.addProperty("authResult", "1");
        request.addProperty("useHours",time.getText().toString().trim());


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
            Log.i("lalala",bbb.toString());
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
                if(lala.equals("")){
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//授权不通过函数
    public String AddUser1() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "AuthoritysEqu";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("authorizedId", KeyId);
        request.addProperty("authResult", "0");
        request.addProperty("useHours","0");


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
            Log.i("lalala",bbb.toString());
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
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
                lala = AddUser1();
                if(lala.equals("")){
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//消息已读函数
    public String LookedMsg() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "LookedMsg";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("msgId",msgID);


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
            Log.i("lalala",bbb.toString());
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
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
                lala = LookedMsg();
                if(lala.equals("")){
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
