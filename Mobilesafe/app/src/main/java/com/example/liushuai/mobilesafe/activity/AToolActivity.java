package com.example.liushuai.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.engine.SmsBackUp;

import java.io.File;

public class AToolActivity extends AppCompatActivity {
    private TextView tv_query_phone_address;
    private TextView tv_sms_backup;
    private ProgressBar pb_bar;
    private TextView tv_app_lock;
    private TextView tv_commonnumber_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_atool);
    //电话归属地查询方法
        initPhoneAddress();
        //短信备份方法
        initSmsBackUp();
        //常用号码查询
        initCommonNumberQuery();
        //程序锁
        initAppLock();
    }

    private void initAppLock() {
        tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sms.xml";
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.Callback() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                        pb_bar.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
                        pb_bar.setProgress(index);
                    }
                });
                progressDialog.dismiss();
            }
        }.start();
    }

    private void initPhoneAddress() {
        tv_query_phone_address = (TextView)findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
            }
        });
    }

}
