package com.example.liushuai.hcble.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.service.ApplyService;
import com.example.liushuai.hcble.service.MessageService;
import com.example.liushuai.hcble.utils.StatusBarUtil;

import java.util.List;

import static com.example.liushuai.hcble.R.drawable.tips_circle;

public class Home extends AppCompatActivity implements View.OnClickListener,
        FirstPage.OnFragmentInteractionListener,Function.OnFragmentInteractionListener,
        Message.OnFragmentInteractionListener,Setting.OnFragmentInteractionListener,Message.FragmentInteraction{
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private LinearLayout shouye;//首页
    private LinearLayout gongneng;//功能
    private LinearLayout xiaoxi;//消息
    private LinearLayout shezhi;//设置
    private ImageView iv_shouye;
    private TextView tv_shouye;
    private ImageView iv_gongneng;
    private TextView tv_gongnegn;
    private ImageView iv_xiaoxi;
    private TextView tv_xiaoxi;
    private ImageView iv_shezhi;
    private TextView tv_shezhi;
    private String usertype;
    private String ID;
    public static TextView count;//显示消息数量
    MessageService.MyBinder mbinder;
    ApplyService.MyBinder nbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        StatusBarUtil.setColor(this, 0x2b2941, 10);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        Log.i("type", usertype);
        //绑定消息服务
        Intent intent1 = new Intent(this, MessageService.class);
        intent1.putExtra("ID",ID);
        bindService(intent1, conn1, Context.BIND_AUTO_CREATE);
        Intent intent2 = new Intent(this, ApplyService.class);
        //绑定申请服务
        intent2.putExtra("ID",ID);
        bindService(intent2, conn2, Context.BIND_AUTO_CREATE);
        initUI();
    }
    private ServiceConnection conn1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder = (MessageService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ServiceConnection conn2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            nbinder = (ApplyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initUI() {
        shouye = (LinearLayout)findViewById(R.id.shouye);
        shouye.setOnClickListener(this);
        iv_shouye = (ImageView)findViewById(R.id.iv_shouye);
        tv_shouye = (TextView)findViewById(R.id.tv_shouye);
        gongneng = (LinearLayout)findViewById(R.id.gongneng);
        gongneng.setOnClickListener(this);
        iv_gongneng = (ImageView)findViewById(R.id.iv_gongneng);
        tv_gongnegn = (TextView)findViewById(R.id.tv_gongneng);
        xiaoxi = (LinearLayout)findViewById(R.id.xiaoxi);
        xiaoxi.setOnClickListener(this);
        iv_xiaoxi = (ImageView)findViewById(R.id.iv_xiaoxi);
        tv_xiaoxi = (TextView)findViewById(R.id.tv_xiaoxi);
        shezhi = (LinearLayout)findViewById(R.id.shezhi);
        shezhi.setOnClickListener(this);
        iv_shezhi = (ImageView)findViewById(R.id.iv_shezhi);
        tv_shezhi = (TextView)findViewById(R.id.tv_shezhi);
        FirstPage shouye = new FirstPage();
        Bundle bundle = new Bundle();
        bundle.putString("usertype",usertype);
        bundle.putString("ID", ID);
        shouye.setArguments(bundle);
        jiaZai(shouye);
        iv_shouye.setImageResource(R.drawable.home);
        tv_shouye.setTextColor(0xFFd24a58);
        count = (TextView)findViewById(R.id.count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shouye:
                iv_shouye.setImageResource(R.drawable.home);
                tv_shouye.setTextColor(0xFFd24a58);
                iv_xiaoxi.setImageResource(R.drawable.message);
                tv_xiaoxi.setTextColor(0xFFffffff);
                iv_shezhi.setImageResource(R.drawable.shezhi);
                tv_shezhi.setTextColor(0xFFffffff);
                iv_gongneng.setImageResource(R.drawable.gongneng);
                tv_gongnegn.setTextColor(0xFFffffff);
                FirstPage shouye = new FirstPage();
                Bundle bundle = new Bundle();
                bundle.putString("usertype",usertype);
                bundle.putString("ID",ID);
                shouye.setArguments(bundle);
                jiaZai(shouye);
                break;
            case R.id.gongneng:
                iv_gongneng.setImageResource(R.drawable.gongneng1);
                tv_gongnegn.setTextColor(0xFFd24a58);
                iv_shouye.setImageResource(R.drawable.home1);
                tv_shouye.setTextColor(0xFFffffff);
                iv_xiaoxi.setImageResource(R.drawable.message);
                tv_xiaoxi.setTextColor(0xFFffffff);
                iv_shezhi.setImageResource(R.drawable.shezhi);
                tv_shezhi.setTextColor(0xFFffffff);
                Function function = new Function();
                Bundle bundle1 = new Bundle();
                bundle1.putString("usertype", usertype);
                bundle1.putString("ID", ID);
                function.setArguments(bundle1);
                jiaZai(function);
                break;
            case R.id.xiaoxi:
                iv_xiaoxi.setImageResource(R.drawable.message1);
                tv_xiaoxi.setTextColor(0xFFd24a58);
                iv_shouye.setImageResource(R.drawable.home1);
                tv_shouye.setTextColor(0xFFffffff);
                iv_gongneng.setImageResource(R.drawable.gongneng);
                tv_gongnegn.setTextColor(0xFFffffff);
                iv_shezhi.setImageResource(R.drawable.shezhi);
                tv_shezhi.setTextColor(0xFFffffff);
                Message message = new Message();
                Bundle bundle2 = new Bundle();
                bundle2.putString("usertype", usertype);
                bundle2.putString("ID", ID);
                message.setArguments(bundle2);
                jiaZai(message);
                break;
            case R.id.shezhi:
                iv_shezhi.setImageResource(R.drawable.shezhi1);
                tv_shezhi.setTextColor(0xFFd24a58);
                iv_shouye.setImageResource(R.drawable.home1);
                tv_shouye.setTextColor(0xFFffffff);
                iv_xiaoxi.setImageResource(R.drawable.message);
                tv_xiaoxi.setTextColor(0xFFffffff);
                iv_gongneng.setImageResource(R.drawable.gongneng);
                tv_gongnegn.setTextColor(0xFFffffff);
                Setting setting = new Setting();
                Bundle bundle3 = new Bundle();
                bundle3.putString("usertype", usertype);
                bundle3.putString("ID", ID);
                setting.setArguments(bundle3);
                jiaZai(setting);
                break;
        }
    }

    public void jiaZai(Fragment fragment){
        manager=getSupportFragmentManager();//获取fragment管理者
        transaction=manager.beginTransaction();//通过管理者开启事务
        transaction.replace(R.id.line1, fragment);
        transaction.commit();//非常关键  这句话的意思提交  没有这句的话  是没有反应的
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    //退出程序时调用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();
            return true;
        }
        return true;
    }
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setMessage("确定要退出本程序吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //AccoutList.this.finish();
                        //System.exit(1);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    public void updata(String str) {
        int i = Integer.parseInt(str);
        if(i>0) {
            count.setText(str);
            count.setBackground(getResources().getDrawable(R.drawable.tips_circle));
        }else{
            count.setText("");
            count.setBackground(null);
        }
    }
}
