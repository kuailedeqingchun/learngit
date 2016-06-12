package com.example.liushuai.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.SpUtil;
import com.example.liushuai.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity{
    private EditText et_phone_number;
    private Button bt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setup3);
        initUI();
    }

    @Override
    protected void showNextPage() {
        String phone = et_phone_number.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //如果现在是输入电话号码,则需要去保存
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }
        else{
            ToastUtil.show(this, "请输入电话号码");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        //显示电话号码的输入框
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);

        //点击选择联系人的对话框
        bt_select_number = (Button)findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
