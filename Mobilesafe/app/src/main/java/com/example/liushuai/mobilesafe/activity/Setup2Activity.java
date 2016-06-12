package com.example.liushuai.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.SpUtil;
import com.example.liushuai.mobilesafe.utils.ToastUtil;
import com.example.liushuai.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setup2);
        initUI();
    }

    @Override
    protected void showNextPage() {
        String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if(!TextUtils.isEmpty(serialNumber)){
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }
        else{
            ToastUtil.show(this,"请绑定sim卡");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1.数据回显
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        //2,判断序列号是否为空
        if(TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);
        }
        else{
            siv_sim_bound.setCheck(true);
        }

        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3,获取原有状态
                boolean isCheck = siv_sim_bound.isCheck();
                //4,将原有状态去反设置给当前条目
                siv_sim_bound.setCheck(!isCheck);
                if (!isCheck) {
                    //5,存储
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(getApplication(), ConstantValue.SIM_NUMBER, simSerialNumber);

                } else {
                    //6,将存储序列卡号的节点从sp中删除
                    SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }


}
