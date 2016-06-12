package com.example.liushuai.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.Md5Util;
import com.example.liushuai.mobilesafe.utils.SpUtil;
import com.example.liushuai.mobilesafe.utils.ToastUtil;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        initUI();
        initData();
    }

    private void initData() {
        //准备数据(文字(9组),图片(9张))
        mTitleStrs = new String[]{
                "手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"
        };

        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };
        //九宫格控件设置数据适配器
        gv_home.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
                        break;
                    case 2:
                        //跳转到通信卫士模块
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        //跳转到进程管理模块
                        startActivity(new Intent(getApplicationContext(),  ProcessManagerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 5:
                        //跳转到病毒查杀模块
                        startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
                        break;
                    case 7:
                        //跳转到高级工具功能列表界面
                        startActivity(new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否有存储密码
        String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if(TextUtils.isEmpty(psd)){
            //初始设置密码对话框
            showSetPsdDialog();
        }
        else{
            showConfirmPsdDialog();
        }
    }

    private void showConfirmPsdDialog() {
        //view由自己编写的XML转换成view对象的xml
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_corfim_psd,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_Psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_Psd.getText().toString();
                if(!TextUtils.isEmpty(confirmPsd)){
                    String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    if(psd.equals(Md5Util.encoder(confirmPsd))){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //隐藏对话框
                        dialog.dismiss();
                        SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, psd);

                    }
                    else{
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                }
                else {
                    //提示用户当前密码为空
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPsdDialog() {
        //view由自己编写的XML转换成view对象的xml
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_set_psd,null);
        //让对话框显示一个自己定义的对话框界面效果
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                if(!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(confirmPsd)){
                    if(psd.equals(confirmPsd)){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //隐藏对话框
                        dialog.dismiss();
                        SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(confirmPsd));

                    }
                    else{
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                }
                else {
                    //提示用户当前密码为空
                    ToastUtil.show(getApplicationContext(),"密码为空");
                }
            }
        });
        
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
        
    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[position]);
            //System.out.println(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }

}
