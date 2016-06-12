package com.example.liushuai.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.SpUtil;
import com.example.liushuai.mobilesafe.utils.StreamUtil;
import com.example.liushuai.mobilesafe.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private TextView tv_version_name;
    private int mLocalVerdionCode;
    private RelativeLayout r1_root;
    protected static final String TAG = "SplashActivity";
    //更新状态的状态码
    protected static final int UPDATE_VERSION = 100;
    //应用进入主界面的状态吗
    protected static final int ENTER_HOME = 101;

    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_VERSION:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
            }
        }
    };

    //弹出对话框，提示用户更新
    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);

        //积极按钮，立即更新
        builder.setPositiveButton("立即跟新",new DialogInterface.OnClickListener(){
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
                enterHome();
            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //进去程序主界面
    private void enterHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void downloadApk() {
        //1,判断sd卡是否可用
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //2,获取sd路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+"mobilesafe.apk";
            //3,发送请求，获取apk,并且放到指定路径
            HttpUtils httpUtils = new HttpUtils();
            //4,发送请求，传递参数
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i(TAG,"下载成功");
                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {

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

    private String mVersionDes;
    private String mDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
        //初始化数据库
        initDB();
    }


    private void initDB() {
        //归属地数据考呗过程
        initAddressDB("address.db");
        initAddressDB("commonnum.db");
        initAddressDB("antivirus.db");
    }

    private void initAddressDB(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if(file.exists()){
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        //2,输入流读取第三方资产目录下的文件
        try {
            stream = getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            fos = new FileOutputStream(file);
            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);
        r1_root.startAnimation(alphaAnimation);
    }

    private void initData() {
        //1.应用版本名称
        tv_version_name.setText("版本名称：" + getVersionName());
        //检测是否有更新，提示下载
        //2,获取本地版本号
        mLocalVerdionCode = getVersionCode();
        //3,获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
        //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
        //json中内容包含:
		/* 更新版本的版本名称
		 * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
        if(SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)){
            checkVersion();
        }else{
            //直接进入应用程序主界面
//			enterHome();
            //消息机制
//			mHandler.sendMessageDelayed(msg, 4000);
            //在发送消息4秒后去处理,ENTER_HOME状态码指向的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }

    }

    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1,封装url地址
                    URL url = new URL("http://10.0.2.2:8080/updata.json");
                    //2,开启一个链接
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    //3.设置常见请求参数
                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);
                    connection.setRequestMethod("POST");
                    //4,获取请求成功响应吗
                    if(connection.getResponseCode() == 200){
                        //Log.i(TAG, "请求成功后");
                        //5，以流的形式，将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6,将流转换成字符串（工具类封装）
                        String json = StreamUtil.streamToString(is);
                        System.out.println(json+"lalala");
                        //Log.i(TAG, "返回成功");
                        //7，json解析
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试
                        String versionName = jsonObject.getString("versionName");
                        //Log.i(TAG, "get成功");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downLoadUrl");

                        //日志打印	
                        Log.i(TAG, versionName);
                        Log.i(TAG, mVersionDes);
                        Log.i(TAG, versionCode);
                        Log.i(TAG, mDownloadUrl);

                        //8,比对版本号
                        if(mLocalVerdionCode<Integer.parseInt(versionCode)){
                            //提示用户更新，弹出对话框
                            msg.what = UPDATE_VERSION;
                        }
                        else {
                            //进入应用程序主界面
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                }
                finally {
                    //指定睡眠时间
                    long endTime = System.currentTimeMillis();
                    if(endTime-startTime<4000){
                        try {
                            Thread.sleep(4000-(endTime-startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

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

    private String getVersionName() {
        //1，包管理者对象packageManager;
        PackageManager pm = getPackageManager();
        //2,获取指定包名的基本信息(版本名称，版本号)
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            //3,获取版本名称
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initUI() {

        tv_version_name   = (TextView)findViewById(R.id.tv_version_name);
        r1_root = (RelativeLayout) findViewById(R.id.r1_root);
    }


}
