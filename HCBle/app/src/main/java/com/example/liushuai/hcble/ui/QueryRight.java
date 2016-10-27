package com.example.liushuai.hcble.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class QueryRight extends AppCompatActivity {
    private Spinner spinner1;//姓名下拉框
    private Spinner spinner2;//设备下拉框
    private Button query;//查询按钮
    private Button btnstart;//开始时间按钮
    private Button btnend;//结束时间按钮
    private TextView tvstart;//开始时间
    private TextView tvend;//结束时间
    private TextView tvequip;//设备显示
    private TextView tvperson;//姓名显示
    private List<EquipmentInfo> mEquipmentInfo;//设备信息列表
    private List<UsersInfo> mUsersInfo;//用户信息列表
    private List<String> equList = new ArrayList<String>();//设备列表
    private List<String> perList = new ArrayList<String>();//用户列表
    private String ID;
    private String starttime = null;//开始时间设为空
    private String endtime = null;//结束时间设为空
    private ArrayAdapter<String> adapter1;//自定义适配器
    private ArrayAdapter<String> adapter2;//自定义设配器
    private String equID;//设备ID
    private String usePhoneId;//使用人ID
    //private DatePicker datePicker;
    JSONArray arr = new JSONArray();//json数组
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
        setContentView(R.layout.content_query_right);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        ID = bundle.getString("ID");
        initUI();
        //获得用户列表
        Thread t=new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
        t.start();
        //获得设备列表
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
        tvstart=(TextView)findViewById(R.id.tvstart);
        tvend = (TextView)findViewById(R.id.tvend);
        tvequip = (TextView)findViewById(R.id.tvequip);
        tvperson = (TextView)findViewById(R.id.tvperson);
        btnstart = (Button) findViewById(R.id.btn1);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });
        btnend = (Button)findViewById(R.id.btn2);
        btnend.setOnClickListener(new View.OnClickListener() {
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
                //Log.i("bbb", "adapt" + tvequip.getText().toString());
                for (int i = 0; i < mEquipmentInfo.size(); i++) {
                    //Log.i("bbb",i+mEquipmentInfo.get(i).getEquipmentName());
                    if(adapter1.getItem(position).equals(mEquipmentInfo.get(i).getEquipmentName())){
                        equID = mEquipmentInfo.get(i).getEquipmentID();
                        Log.i("bbb",equID+"lalal");
                        break;
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
                        Log.i("bbb",usePhoneId+"lalal");
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
    //时间选择弹窗
    private void showDialog1() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tvstart.setText(builder.getStr());
                starttime = tvstart.getText().toString();
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
    //时间选择弹窗
    private void showDialog2() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tvend.setText(builder.getStr());
                endtime = tvend.getText().toString();
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
    //获取申请人函数
    public JSONArray AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListEquApply";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId", equID);
        request.addProperty("usePhoneId",usePhoneId);
        request.addProperty("applyTime1",starttime);
        request.addProperty("applyTime2",endtime);
        Log.i("bbb",equID+usePhoneId+starttime+endtime);
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
            Log.i("bbb",aaa);
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
                //RightRecord.mRightInfo = new ArrayList<RightInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                RightRecord.mRightInfo.clear();
                for (int i = 0; i < arr.length(); i++) {
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
                        //RightRecord.mRightInfo.clear();
                        RightRecord.mRightInfo.add(rightInfo);
                         Log.i("bbb", "siuze " + RightRecord.mRightInfo.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(3);
        }
    }
    //获取设备函数
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
                mEquipmentInfo = new ArrayList<EquipmentInfo>();
                Log.i("lalala", "" + "length" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject temp = (JSONObject) arr.get(i);
                        // Log.i("lalala",temp.toString());
                        //Log.i("lalala",temp.getString("equipmentCode"));
                        EquipmentInfo equipmentInfo = new EquipmentInfo();
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
            Log.i("bbb","siuze "+mEquipmentInfo.size());
            mHandler.sendEmptyMessage(1);
        }
    }
//获取使用人函数
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
            Log.i("ccc",aaa);
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
