package com.example.liushuai.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.service.LockScreenService;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.ServiceUtil;
import com.example.liushuai.mobilesafe.utils.SpUtil;

public class ProcessSettingActivity extends AppCompatActivity {
    private CheckBox cb_show_system,cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_process_setting);
        initSystemShow();
        initLockScreenClear();
    }

    private void initLockScreenClear() {
        cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
        boolean isRunning = ServiceUtil.isRunning(this, "com.example.liushuai.mobilesafe.service.LockScreenService");
        if(isRunning){
            cb_lock_clear.setText("锁屏清理已开启");
        }
        else{
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        cb_lock_clear.setChecked(isRunning);
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }
                else{
                    cb_lock_clear.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }

    private void initSystemShow() {
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        boolean showSystem = SpUtil.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
        cb_show_system.setChecked(showSystem);

        if(showSystem){
            cb_show_system.setText("显示系统进程");
        }
        else{
            cb_show_system.setText("隐藏系统进程");
        }
        //对选中状态进行监听
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_show_system.setText("显示系统进程");
                }
                else{
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(ProcessSettingActivity.this,ConstantValue.SHOW_SYSTEM, isChecked);
            }
        });
    }

}
