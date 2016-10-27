package com.example.liushuai.hcble.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.RightInfo;
import com.example.liushuai.hcble.model.UseInfo;
import com.example.liushuai.hcble.model.UsersInfo;
import com.example.liushuai.hcble.utils.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QueryUse extends AppCompatActivity {
    private Spinner spinner1;//姓名下拉框
    private Spinner spinner2;//设备下拉框
    private Button query;//查询按钮
    private Button btntime;//开始时间
    private Button btnendtime;//结束时间
    private TextView tvtime;//开始时间显示框
    private TextView tvendtime;//结束时间显示框
    private TextView tvequip;//设备显示框
    private TextView tvperson;//设备显示框
    private List<EquipmentInfo> mEquipmentInfo;
    private List<UsersInfo> mUsersInfo;
    private List<String> equList = new ArrayList<String>();
    private List<String> perList = new ArrayList<String>();
    private String ID;
    private String usetime = null;//开始时间置为空
    private String endtime = null;//结束时间初始化为空
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private String equID;
    private String usePhoneId;
    //private DatePicker datePicker;
    JSONArray arr = new JSONArray();
    //消息处理机制
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    adapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item, equList);
                    //adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    spinner1.setAdapter(adapter1);
                    spinner1.setSelection(0, true);
                    break;
                case 2:
                    adapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item, perList);
                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(adapter2);
                    spinner2.setSelection(0, true);
                    break;
                case 3:
                    Intent mIntent = new Intent();
                    mIntent.putExtra("str", "1000");
                    // 设置结果，并进行传送
                    setResult(0, mIntent);
                    // this.finish();
                    finish();
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_query_use);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        ID = bundle.getString("ID");
        initUI();
        Thread t=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
        t.start();
        Thread t2=new Thread(new MyRunnable2());//这里比第一种创建线程对象多了个任务对象
        t2.start();
//        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);
//        if (datePicker != null) {
//            datePicker.init(year, month, day, new OnDateChangedListener() {
//                @Override
//                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                }
//            });
//        }

    }

    private void initUI() {
        tvtime=(TextView)findViewById(R.id.tvstart);
        tvendtime = (TextView)findViewById(R.id.tvendtime);
        tvequip = (TextView)findViewById(R.id.tvequip);
        tvperson = (TextView)findViewById(R.id.tvperson);
        btntime = (Button) findViewById(R.id.btntime);
        btntime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });
        btnendtime = (Button)findViewById(R.id.btnendtime);
        btnendtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog2();
            }
        });
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("str", "1000");
                // 设置结果，并进行传送
                setResult(0, mIntent);
                // this.finish();
                finish();
            }
        });
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvequip.setText(adapter1.getItem(position));
                for (int i = 0; i < mEquipmentInfo.size(); i++) {
                    if(adapter1.getItem(position).equals(mEquipmentInfo.get(i).getEquipmentName())){
                        equID = mEquipmentInfo.get(i).getEquipmentID();
                        Log.i("bbb",equID+"lalal");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvperson.setText(adapter2.getItem(position));
                for(int i = 0;i < mUsersInfo.size();i++){
                    if(adapter2.getItem(position).equals(mUsersInfo.get(i).getUserName())){
                        usePhoneId = mUsersInfo.get(i).getUserID();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        query = (Button)findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                t.start();
            }
        });
    }

    private void showDialog1() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tvtime.setText(builder.getStr());
                usetime = tvtime.getText().toString();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        showDialog(builder);
    }
    private void showDialog2() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tvendtime.setText(builder.getStr());
                endtime = tvendtime.getText().toString();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        showDialog(builder);
    }

    private void showDialog(CustomDialog.Builder builder) {
        Dialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.show();
    }
    //获取设备使用列表函数
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
        request.addProperty("equId", equID);
        request.addProperty("authorizedId","");
        request.addProperty("usePhoneId",usePhoneId);
        request.addProperty("useTime1",usetime);
        request.addProperty("useTime2",endtime);
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
                UseRecord.mUseinfoList.clear();
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
                        UseRecord.mUseinfoList.add(useInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("lalala","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(3);
        }
    }
    //获取设备列表函数
    public JSONArray Getepu() {
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

    public class MyRunnable1 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = Getepu();
                mUsersInfo = new ArrayList<UsersInfo>();
                EquipmentInfo equipmentInfo = new EquipmentInfo();
                Log.i("lalala", "" + "length" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        // Log.i("lalala",temp.toString());
                        //Log.i("lalala",temp.getString("equipmentCode"));
                        mEquipmentInfo = new ArrayList<EquipmentInfo>();
                        equipmentInfo.setEquipmentCode(temp.getString("equipmentCode"));
                        Log.i("lalala", "EquipmentCode" + equipmentInfo.getEquipmentCode());
                        equipmentInfo.setEquipmentID(temp.getString("equId"));
                        equipmentInfo.setEquipmentName(temp.getString("equipmentName"));
                        equipmentInfo.setEquipmentType(temp.getString("equipmentType"));
                        equipmentInfo.setSsCustomer("李四");
                        equipmentInfo.setUseState(temp.getString("useState"));
                        equList.add(equipmentInfo.getEquipmentName());
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
            mHandler.sendEmptyMessage(1);
        }
    }
//获取姓名函数
    public JSONArray Getper() {
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
            Log.i("aaa",aaa);
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

    public class MyRunnable2 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = Getper();
                mUsersInfo = new ArrayList<UsersInfo>();
                Log.i("lalala",""+"length"+arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        UsersInfo usersInfo = new UsersInfo();
                        usersInfo.setUserID(temp.getString("PhoneId"));
                        usersInfo.setUserName(temp.getString("userName"));
                        perList.add(usersInfo.getUserName());
                        mUsersInfo.add(usersInfo);
                        // Log.i("lalala","siuze "+mEquipmentInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(2);
        }
    }

}
