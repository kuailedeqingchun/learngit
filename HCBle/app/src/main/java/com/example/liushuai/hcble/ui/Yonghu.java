package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.engine.YonghuProvider;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.YonghuList;
import com.example.liushuai.hcble.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Yonghu extends AppCompatActivity implements View.OnClickListener{
    private Button bt_select_all;//全选按钮
    private Button bt_select_reverse;//反选按钮
    private Button bt_clear;//清除按钮
    private Button add;//添加按钮
    private Button back;//返回按钮
    private ListView lv;
    private List<YonghuList> mYonghuList;//自定义变量
    private List<YonghuList> oYonghuList;//自定义变量
    private YonghuList nYonghuList;
    private MyAdapter mAdapter;//listview适配器
    private String ID;//用户ID
    private String usertype;//用户类型
    private String usePhongId;//被添加用户的手机号
    JSONArray arr = new JSONArray();//json数组
    //消息处理机制
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new MyAdapter();
            lv.setAdapter(mAdapter);

        };
    };
    //按钮监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            case R.id.back:
                this.finish();
        }
    }
    //一键清除
    private void clearAll() {
        oYonghuList = new ArrayList<YonghuList>();
        for(YonghuList yonghu:mYonghuList){
            if(yonghu.isCheck==true){
                mYonghuList.remove(yonghu);
                oYonghuList.add(yonghu);
            }
        }
        //2,通知数据适配器刷新
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        //3.告知服务器
        for(YonghuList yonghu:oYonghuList){
            usePhongId = yonghu.getYonghuID();
            Thread t=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
            t.start();
        }

    }
    //反选函数
    private void selectReverse() {
        for(YonghuList yonghu:mYonghuList){
            yonghu.isCheck=!yonghu.isCheck;
        }
        //2,通知数据适配器刷新
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }
    //全选函数
    private void selectAll() {
        for(YonghuList yonghu:mYonghuList){
            yonghu.isCheck=true;
        }
        //2,通知数据适配器刷新
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }
    //自定义适配器
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mYonghuList.size();
        }

        @Override
        public YonghuList getItem(int position) {
            return mYonghuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_yonghugl, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
                holder.tv_phoneNumber = (TextView)convertView.findViewById(R.id.tv_phoneNumber);
                holder.tv_bumenTpye = (TextView) convertView.findViewById(R.id.tv_bumenType);
                holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(getItem(position).pname);
            holder.tv_phoneNumber.setText(getItem(position).phoneNumber);
            holder.tv_bumenTpye.setText(getItem(position).bumenType);
            holder.cb_box.setChecked(getItem(position).isCheck);

            return convertView;
        }
    }
    static class ViewHolder{
        TextView tv_name;
        TextView tv_phoneNumber;
        TextView tv_bumenTpye;
        CheckBox cb_box;//是否选中
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_yonghu);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        //初始化UI
        init();
        //初始化数据
        initData();
    }

    private void initData() {
        mYonghuList = YonghuProvider.getyonghuList();
        Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
        t.start();
        //mHandler.sendEmptyMessage(0);
    }

    private void init() {
        add = (Button)findViewById(R.id.bt_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),AddYonghu.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
                startActivity(intent);;
            }
        });
        lv = (ListView)findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.getItem(position).isCheck = !mAdapter.getItem(position).isCheck;
                mAdapter.notifyDataSetChanged();
            }
        });
        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clear = (Button)  findViewById(R.id.bt_clear);
        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);

    }
//获取本部门用户列表
    public JSONArray AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListUser";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        //String jieguo=null;
        JSONArray jieguo = new JSONArray();
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            Log.i("lalala",aaa);
            JSONObject bbb = new JSONObject(aaa);
            String ccc = bbb.getString("arr");
            Log.i("lalala",ccc);
            jieguo = new JSONArray(ccc);
            //jieguo = new JSONArray(bbb);
        } catch (Exception e) {
            //Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }

    public class MyRunnable implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = AddUser();
                mYonghuList = new ArrayList<YonghuList>();
                Log.i("lalala",""+"length"+arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                         Log.i("lalala",temp.toString());
                        //Log.i("lalala",temp.getString("equipmentCode"));
                        YonghuList yonghuList = new YonghuList();
                        yonghuList.setPname(temp.getString("userName"));
                        yonghuList.setBumenType(temp.getString("customerName"));
                        yonghuList.setPhoneNumber(temp.getString("PhoneId"));
                        yonghuList.setYonghuID(temp.getString("PhoneId"));

                        mYonghuList.add(yonghuList);
                        // Log.i("lalala","siuze "+mEquipmentInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("lalala","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(0);
        }
    }
    //删除用户
    public String Delete() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "DisableUser";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("userPhoneId",usePhongId);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        //JSONArray jieguo = new JSONArray();
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            Log.i("lalala",aaa);
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
            //Log.i("lalala",ccc);
            //jieguo = new JSONArray(ccc);
            //jieguo = new JSONArray(bbb);
        } catch (Exception e) {
            //Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }

    public class MyRunnable1 implements Runnable {
        public void run() {
            String arr = Delete();
            if(arr.equals("")){
                mHandler.sendEmptyMessage(0);
            }else{
                Log.i("lalala",arr);
            }
        }
    }

}
