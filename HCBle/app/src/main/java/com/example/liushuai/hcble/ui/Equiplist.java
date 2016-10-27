package com.example.liushuai.hcble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.engine.YonghuProvider;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.YonghuList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class Equiplist extends AppCompatActivity {
    private ListView lv;
    private MyAdapter mAdapter;//自定义适配器
    private String ID;
    private String usertype;
    private List<EquipmentInfo> mEquipmentInfo;
    JSONArray arr = new JSONArray();//json数组
    //消息处理机制
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Log.i("lalala","siuuuuze "+mEquipmentInfo.size());
            mAdapter = new MyAdapter();
            lv.setAdapter(mAdapter);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_equiplist);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        lv = (ListView)findViewById(R.id.lv);
        //开启一个新线程，获得设备
        Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
        t.start();
        //初始化控件
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
       //Log.i("lalala", "siuze " + mEquipmentInfo.size());
        //mEquipmentInfo = EquipmentProvider.getequipList();

    }

        class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //Log.i("lalala","siuuuuuuuuuze "+mEquipmentInfo.size());
            return mEquipmentInfo.size();
        }

        @Override
        public EquipmentInfo getItem(int position) {
            return mEquipmentInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_equiplist, null);
                holder = new ViewHolder();
                holder.EquipmentName = (TextView)convertView.findViewById(R.id.tv_equipName);
                holder.EquipmentID = (TextView)convertView.findViewById(R.id.equipID);
                holder.EquipmentType = (TextView) convertView.findViewById(R.id.equipType);
                holder.EquipmentCode = (TextView) convertView.findViewById(R.id.equipCode);
                holder.SsCustomer = (TextView) convertView.findViewById(R.id.ssCustomer);
                holder.UseState = (TextView) convertView.findViewById(R.id.useState);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.EquipmentName.setText(getItem(position).equipmentName);
            holder.EquipmentID.setText(getItem(position).equipmentID);
            holder.EquipmentType.setText(getItem(position).equipmentType);
            holder.EquipmentCode.setText(getItem(position).equipmentCode);
            holder.SsCustomer.setText(getItem(position).ssCustomer);
            if(getItem(position).useState.equals("0")){
                holder.UseState.setText("未使用");
            }else if(getItem(position).useState.equals("1")){
                holder.UseState.setText("使用中");
            }else if(getItem(position).useState.equals("2")){
                holder.UseState.setText("锁定");
            }else if(getItem(position).useState.equals("9")){
                holder.UseState.setText("报废");
            }


            return convertView;
        }
    }
    static class ViewHolder{
        TextView EquipmentCode;
        TextView EquipmentID;
        TextView EquipmentName;
        TextView EquipmentType;
        TextView SsCustomer;
        TextView UseState;
    }
    //获取设备列表函数
    public JSONArray AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEqu";
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
                mEquipmentInfo = new ArrayList<EquipmentInfo>();
                Log.i("lalala",""+"length"+arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                       // Log.i("lalala",temp.toString());
                        //Log.i("lalala",temp.getString("equipmentCode"));
                        EquipmentInfo equipmentInfo = new EquipmentInfo();
                        equipmentInfo.setEquipmentCode(temp.getString("equipmentCode"));
                        Log.i("lalala", "EquipmentCode"+equipmentInfo.getEquipmentCode());
                        equipmentInfo.setEquipmentID(temp.getString("equId"));
                        equipmentInfo.setEquipmentName(temp.getString("equipmentName"));
                        equipmentInfo.setEquipmentType(temp.getString("equipmentType"));
                        equipmentInfo.setSsCustomer("李四");
                        equipmentInfo.setUseState(temp.getString("useState"));
                        mEquipmentInfo.add(equipmentInfo);
                       // Log.i("lalala","siuze "+mEquipmentInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("lalala","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(0);
        }
    }

}
