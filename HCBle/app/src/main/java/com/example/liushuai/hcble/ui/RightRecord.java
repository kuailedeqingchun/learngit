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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.engine.RightProvider;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.RightInfo;
import com.example.liushuai.hcble.model.YonghuList;
import com.example.liushuai.hcble.utils.ToastUtil;

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

public class RightRecord extends AppCompatActivity {
    private Button query;//查询按钮
    private ListView lv;
    public static MyAdapter mAdapter;
    public static List<RightInfo> mRightInfo;//授权信息列表
    private String ID;//用户ID
    private String usertype;//用户类型
    JSONArray arr = new JSONArray();
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
            return mRightInfo.size();
        }

        @Override
        public RightInfo getItem(int position) {
            return mRightInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_sqrecord, null);
                holder = new ViewHolder();
                holder.applyName = (TextView)convertView.findViewById(R.id.applyName);
                holder.sqName = (TextView)convertView.findViewById(R.id.sqName);
                holder.sqNumber = (TextView) convertView.findViewById(R.id.sqNumber);
                holder.sqReason = (TextView) convertView.findViewById(R.id.sqReason1);
                holder.sqCode = (TextView) convertView.findViewById(R.id.sqCode);
                holder.sqReault = (TextView) convertView.findViewById(R.id.sqReault);
                holder.equipmentID = (TextView) convertView.findViewById(R.id.equipmentID);
                holder.sqtime = (TextView)convertView.findViewById(R.id.sqtime);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.applyName.setText(getItem(position).applyName);
            holder.sqName.setText(getItem(position).sqName);
            holder.sqNumber.setText(getItem(position).sqNumber);
            holder.sqReason.setText(getItem(position).sqReason);
            holder.sqCode.setText(getItem(position).sqCode);
            if(getItem(position).sqResult.equals("0")){
                holder.sqReault.setText("不同意");
            }else if(getItem(position).sqResult.equals("1")){
                holder.sqReault.setText("同意");
            }
            holder.equipmentID.setText(getItem(position).equipmentID);
            holder.sqtime.setText(getItem(position).sqtime);

            return convertView;
        }
    }
    static class ViewHolder{
        TextView applyName;
        TextView sqName;
        TextView sqNumber;
        TextView sqReason;
        TextView sqCode;
        TextView sqReault;
        TextView equipmentID;
        TextView sqtime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_right_record);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        usertype = bundle.getString("usertype");//getString()返回指定key的值
        ID = bundle.getString("ID");
        lv = (ListView)findViewById(R.id.lv);
        //初始化UI
        init();
//        Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
//        t.start();
        if(usertype.equals("9")){
            Thread t = new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
            t.start();
        }else {
            Thread t = new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
            t.start();
        }
    }


    private void init() {
        //lv = (ListView)findViewById(R.id.lv);
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
                Intent intent = new Intent(getApplicationContext(), QueryRight.class);
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
    }
//管理员获取授权记录函数
    public JSONArray AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquApply";
        SoapObject request = new SoapObject(nameSpace, method);
        SimpleDateFormat shijian = new SimpleDateFormat("yyyy-MM-dd");
        String date = shijian.format(new Date());
        request.addProperty("PhoneId", ID);
        request.addProperty("equId", "");
        request.addProperty("usePhoneId","");
        request.addProperty("applyTime1","");
        request.addProperty("applyTime2",date);
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
                mRightInfo = new ArrayList<RightInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                if(arr.length()>=1) {
                    for (int i = arr.length() - 1; i >= 0; i--) {
                        try {
                            JSONObject temp = (JSONObject) arr.get(i);
                            RightInfo rightInfo = new RightInfo();
                            rightInfo.setApplyName(temp.getString("applyName"));
                            rightInfo.setEquipmentID(temp.getString("equipmentID"));
                            rightInfo.setSqCode(temp.getString("authorizedId"));
                            rightInfo.setSqName(temp.getString("authName"));
                            rightInfo.setSqNumber(temp.getString("applyTimes"));
                            rightInfo.setSqReason(temp.getString("applyReason"));
                            rightInfo.setSqResult(temp.getString("authResult"));
                            rightInfo.setSqtime(temp.getString("authStartTime"));
                            mRightInfo.add(rightInfo);
                            // Log.i("lalala","siuze "+mEquipmentInfo.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("lalala","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(0);
        }
    }
//普通用户获取授权记录接口
    public JSONArray AddUser1() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquApply";
        SoapObject request = new SoapObject(nameSpace, method);
        SimpleDateFormat shijian = new SimpleDateFormat("yyyy-MM-dd");
        String date = shijian.format(new Date());
        request.addProperty("PhoneId", ID);
        request.addProperty("equId", "");
        request.addProperty("usePhoneId",ID);
        request.addProperty("applyTime1","");
        request.addProperty("applyTime2",date);
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
                mRightInfo = new ArrayList<RightInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                if(arr.length()>=1) {
                    for (int i = arr.length() - 1; i >= 0; i--) {
                        try {
                            JSONObject temp = (JSONObject) arr.get(i);
                            RightInfo rightInfo = new RightInfo();
                            rightInfo.setApplyName(temp.getString("applyName"));
                            rightInfo.setEquipmentID(temp.getString("equipmentID"));
                            rightInfo.setSqCode(temp.getString("authorizedId"));
                            rightInfo.setSqName(temp.getString("authName"));
                            rightInfo.setSqNumber(temp.getString("applyTimes"));
                            rightInfo.setSqReason(temp.getString("applyReason"));
                            rightInfo.setSqResult(temp.getString("authResult"));
                            rightInfo.setSqtime(temp.getString("authStartTime"));
                            mRightInfo.add(rightInfo);
                            // Log.i("lalala","siuze "+mEquipmentInfo.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("lalala","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 0:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                String str=b.getString("str");//str即为回传的值
                //ToastUtil.show(getApplicationContext(),str);
                Log.i("bbb","length"+mRightInfo.size());
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
