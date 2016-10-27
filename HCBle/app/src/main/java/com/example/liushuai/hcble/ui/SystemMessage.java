package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liushuai.hcble.R;

public class SystemMessage extends AppCompatActivity {
    private TextView systemmessage;
    private String ID;
    private String usertype;
    private String lala;
    private String msgID;
    private String KeyId;
    private String msgBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_system_message);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        msgID = bundle.getString("msgId");
        KeyId = bundle.getString("KeyId");
        msgBody = bundle.getString("msgBody");
        systemmessage = (TextView)findViewById(R.id.systemmessage);
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
        systemmessage.setText(msgBody);
    }

}
