package com.example.liushuai.hcble.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.utils.ConstantValue;
import com.example.liushuai.hcble.utils.SpUtil;
import com.example.liushuai.hcble.utils.StreamUtil;
import com.example.liushuai.hcble.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Versioninformation extends AppCompatActivity {
    private int mLocalVerdionCode;//本地版本号
    private TextView nowversion;//当前版本显示框
    private Button checkupdata;//检查跟新按钮
    private String mVersionDes;//版本描述
    private String versionCode;//版本号
    private String versionName;//版本名称
    private String mDownloadUrl;//下载地址
    private String ID;//用户ID
    private String aaa;//自定义变量
    String lala = null;//自定义变量
    JSONObject jieguo;//json对象
    JSONObject arr;//json对象

    //版本跟新对话框
    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);

        //积极按钮，立即更新
        builder.setPositiveButton("立即更新",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk
                downloadApk();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框，进入主界面
                dialog.dismiss();
            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                ToastUtil.show(getApplicationContext(), lala);
            }else if(msg.what == 2){
                mLocalVerdionCode = getVersionCode();
                Log.i("la1",mLocalVerdionCode+"");
                Log.i("la1",versionCode+"lala");
                //对比版本号
                if(mLocalVerdionCode<Integer.parseInt(versionCode)){
                    //提示用户更新，弹出对话框
                    showUpdateDialog();
                    //ToastUtil.show(getApplicationContext(),versionCode);
                }
                else {
                    //进入应用程序主界面
                    ToastUtil.show(getApplicationContext(),"没有最新版本");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_versioninformation);
        //getSupportActionBar().setTitle("版本信息");
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        ID = bundle.getString("ID");
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
        nowversion = (TextView)findViewById(R.id.versionCode);
        nowversion.setText("当前版本：" + getVersionCode());
        checkupdata = (Button)findViewById(R.id.checkupdata);
        checkupdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
    }

    private void checkVersion() {
        //获取最新版本信息
        Thread t=new Thread(new MyRunnable());
        t.start();

//        String versionName = Version[2];
//        //Log.i(TAG, "get成功");
//        mVersionDes = Version[3];
//        versionCode = Version[1];
//        mDownloadUrl = Version[4];


//                        Log.i(TAG, versionName);
//                        Log.i(TAG, mVersionDes);
//                        Log.i(TAG, versionCode);
//                        Log.i(TAG, mDownloadUrl);

        //8,比对版本号

    }




//获得当前版本号
    private int getVersionCode() {
        //1，包管理者对象packageManager;
        PackageManager pm = getPackageManager();
        //2,获取指定包名的基本信息(版本名称，版本号)
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            //3,获取版本名称
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
//下载apk
    private void downloadApk() {
        //1,判断sd卡是否可用
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //2,获取sd路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+"hcble.apk";
            //3,发送请求，获取apk,并且放到指定路径
            HttpUtils httpUtils = new HttpUtils();
            //4,发送请求，传递参数
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i("xiazai","下载成功");
                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("xiazai","下载失败");
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }
//安装apk
    private void installApk(File file) {
        //系统应用界面，源码，安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        //intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);
        startActivityForResult(intent,0);
    }
    //获取最新版本函数
    public String Login() {
        Log.i("lalala","运行啦");
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetLastVersion";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId",ID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        try {
            ht.call(nameSpace + method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            aaa = result.toString();
            Log.i("la1",aaa);
            jieguo = new JSONObject(aaa);
            Log.i("la1",jieguo.getString("versioncode")+"llll");
        } catch (Exception e) {
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return aaa;
    }
    public class MyRunnable implements Runnable {
        public void run() {
            //你需要实现的代码
            try {
                arr = new JSONObject(Login());
                lala = arr.getString("errmsg");
                versionCode = arr.getString("versioncode");
                versionName = arr.getString("versionname");
                mVersionDes = arr.getString("versiondesp");
                mDownloadUrl = arr.getString("downloadurl");
                if(!lala.equals("")){
                    handler.sendEmptyMessage(1);
                }else{
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala",lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
