package com.example.liushuai.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.engine.VirusDao;
import com.example.liushuai.mobilesafe.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnitVirusActivity extends AppCompatActivity {
    protected static final int SCANING = 100;

    protected static final int SCAN_FINISH = 101;
    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    private List<ScanInfo> mVirusScanInfoList;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_anit_virus);
        initUI();
        initAnimation();
        checkVirus();
    }
    class ScanInfo{
        public boolean isVirus;
        public String packageName;
        public String name;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANING:
                    //显示正在扫描应用的名称
                    ScanInfo info = (ScanInfo) msg.obj;
                    tv_name.setText(info.name);
                    //添加一个真在扫描应用的textview
                    TextView textView = new TextView(getApplicationContext());
                    if(info.isVirus){
                        textView.setTextColor(Color.red(5));
                        textView.setText("发现病毒:"+info.name);
                    }
                    else{
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全:"+info.name);
                    }
                    ll_add_text.addView(textView,0);
                    break;
                case SCAN_FINISH:
                    tv_name.setText("扫描完成");
                    //停止动画
                    iv_scanning.clearAnimation();
                    unInstallVirus();
                    break;
            }
        }
    };

    private void unInstallVirus() {
        for(ScanInfo scanInfo:mVirusScanInfoList){
            String packageName = scanInfo.packageName;
            //源码
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    private void checkVirus() {
        new Thread(){
            @Override
            public void run() {
                List<String> virusList = VirusDao.getVirusList();
                PackageManager pm = getPackageManager();
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                //创建记录病毒的集合
                mVirusScanInfoList = new ArrayList<ScanInfo>();
                //记录所有应用的集合
                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
                //设置进度条的最大值
                pb_bar.setMax(packageInfoList.size());
                //遍历应用集合
                for (PackageInfo packageInfo : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();
                    //获取签名文件的数组
                    Signature[] signatures = packageInfo.signatures;
                    //获取签名文件数组的第一位,然后进行md5,将此md5和数据库中的md5比对
                    Signature signature = signatures[0];
                    String string = signature.toCharsString();
                    //32位字符串,16进制字符(0-f)
                    String encoder = Md5Util.encoder(string);
                    //4,比对应用是否为病毒
                    if (virusList.contains(encoder)) {
                        //5.记录病毒
                        scanInfo.isVirus = true;
                        mVirusScanInfoList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }

                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);

                    //更新进度调
                    index++;
                    pb_bar.setProgress(index);
                    try {
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //告知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //指定动画一直旋转
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        //保持动画执行结束后的状态
        rotateAnimation.setFillAfter(true);
        //一直执行动画
        iv_scanning.startAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
    }

}
