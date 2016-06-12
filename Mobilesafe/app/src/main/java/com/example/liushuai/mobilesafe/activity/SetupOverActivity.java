package com.example.liushuai.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.SpUtil;

public class SetupOverActivity extends Activity {
    private TextView tv_phone;
    private TextView tv_reset_setup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_test);
        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if(setup_over){
            //密码输入成功，并且四个导航界面设置完成
            setContentView(R.layout.content_test);
            initUI();
        }
        else{
            //四个导航界面没有完成
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            //开启了一个新的界面以后,关闭功能列表界面
            finish();
        }
    }

    private void initUI() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        //设置联系人号码
        String phone = SpUtil.getString(this,ConstantValue.CONTACT_PHONE, "");
        tv_phone.setText(phone);
        //重新设置条目被点击
        tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
