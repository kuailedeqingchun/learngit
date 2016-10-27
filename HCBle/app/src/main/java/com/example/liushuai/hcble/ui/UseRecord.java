package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.engine.UseInfoProvider;
import com.example.liushuai.hcble.model.RightInfo;
import com.example.liushuai.hcble.model.UseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UseRecord extends AppCompatActivity {
    private Button query;//查询按钮
    private MyAdapter mAdapter;//自定义适配器
    private ListView lv;
    public static List<UseInfo> mUseinfoList;//使用信息列表
    private String ID;//用户ID
    private String usertype;//用户类型
    JSONArray arr = new JSONArray();//json数组
    //消息处理机制
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new MyAdapter();
            lv.setAdapter(mAdapter);

        };
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUseinfoList.size();
        }

        @Override
        public UseInfo getItem(int position) {
            return mUseinfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_userecord, null);
                holder = new ViewHolder();
                holder.userName = (TextView)convertView.findViewById(R.id.userName);
                holder.userID = (TextView)convertView.findViewById(R.id.userID);
                holder.equipID = (TextView) convertView.findViewById(R.id.equipID);
                holder.usertime = (TextView) convertView.findViewById(R.id.usertime);
                holder.useradress = (TextView) convertView.findViewById(R.id.useradress);
                holder.sqreason = (TextView) convertView.findViewById(R.id.sqreason);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.userName.setText(getItem(position).useName);
            holder.userID.setText(getItem(position).useID);
            holder.equipID.setText(getItem(position).equipmentID);
            holder.usertime.setText(getItem(position).useTime);
            holder.useradress.setText(getItem(position).useAdress);
            holder.sqreason.setText(getItem(position).sqReason);

            return convertView;
        }
    }
    static class ViewHolder{
        TextView userName;
        TextView userID;
        TextView equipID;
        TextView usertime;
        TextView useradress;
        TextView sqreason;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_use_record);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        lv = (ListView)findViewById(R.id.lv);
        if(usertype.equals("9")){
            Thread t = new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
            t.start();
        }else {
            Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
            t.start();
        }
        init();
    }


    private void init() {
        lv = (ListView)findViewById(R.id.lv);
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        query = (Button)findViewById(R.id.bt_query);
        if(usertype.equals("9")){
            query.setEnabled(false);
        }
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueryUse.class);
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
    }
    //管理员获取使用记录函数
    public JSONArray AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquUse";
        SoapObject request = new SoapObject(nameSpace, method);
        SimpleDateFormat shijian = new SimpleDateFormat("yyyy-MM-dd");
        String date = shijian.format(new Date());
        request.addProperty("PhoneId", ID);
        request.addProperty("equId", "");
        request.addProperty("authorizedId","");
        request.addProperty("usePhoneId","");
        request.addProperty("useTime1",date);
        request.addProperty("useTime2",date);
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
            Log.i("lalala", aaa);
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
                Log.i("ccc",arr.toString());
                mUseinfoList = new ArrayList<UseInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        UseInfo useInfo = new UseInfo();
                        useInfo.setUseName(temp.getString("userName"));
                        useInfo.setSqReason(temp.getString("applyReason"));
                        useInfo.setEquipmentID("1");
                        useInfo.setUseID(temp.getString("applyPhoneID"));
                        useInfo.setUseTime(temp.getString("useTime"));
                        useInfo.setUseAdress(temp.getString("useLocation"));
                        mUseinfoList.add(useInfo);
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
//普通用户获取使用记录函数
    public JSONArray AddUser1() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquUse";
        SoapObject request = new SoapObject(nameSpace, method);
        SimpleDateFormat shijian = new SimpleDateFormat("yyyy-MM-dd");
        String date = shijian.format(new Date());
        request.addProperty("PhoneId", ID);
        request.addProperty("equId", "");
        request.addProperty("authorizedId","");
        request.addProperty("usePhoneId",ID);
        request.addProperty("useTime1","");
        request.addProperty("useTime2",date);
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
            Log.i("lalala", aaa);
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

    public class MyRunnable1 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = AddUser1();
                Log.i("ccc",arr.toString());
                mUseinfoList = new ArrayList<UseInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        UseInfo useInfo = new UseInfo();
                        useInfo.setUseName(temp.getString("userName"));
                        useInfo.setSqReason(temp.getString("applyReason"));
                        useInfo.setEquipmentID("1");
                        useInfo.setUseID(temp.getString("applyPhoneID"));
                        useInfo.setUseTime(temp.getString("useTime"));
                        useInfo.setUseAdress(temp.getString("useLocation"));
                        mUseinfoList.add(useInfo);
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
//接受子页面传回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 0:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                String str=b.getString("str");//str即为回传的值
                Log.i("bbb","length"+mUseinfoList.size());
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }


}
