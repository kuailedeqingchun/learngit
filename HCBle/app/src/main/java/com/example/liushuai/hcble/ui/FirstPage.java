package com.example.liushuai.hcble.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.MessageInfo;
import com.example.liushuai.hcble.model.RightInfo;
import com.example.liushuai.hcble.model.UsersInfo;
import com.example.liushuai.hcble.service.ApplyService;
import com.example.liushuai.hcble.service.BluetoothLeService;
import com.example.liushuai.hcble.service.MessageService;
import com.example.liushuai.hcble.utils.ConstantValue;
import com.example.liushuai.hcble.utils.Md5Util;
import com.example.liushuai.hcble.utils.MyLocationListener;
import com.example.liushuai.hcble.utils.SpUtil;
import com.example.liushuai.hcble.utils.ToastUtil;
import com.example.liushuai.hcble.utils.Tobytes;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Function.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Function#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstPage extends Fragment implements View.OnClickListener{
    MediaPlayer mediaPlayer;//多媒体播放器
    AudioManager audioManager;//音频播放器
    int currentVolume;//音量大小
    SharedPreferences preferences;//保存轻量级数据
    SharedPreferences.Editor editor;
    private Banner banner;//广告轮播
    private TextView sqNumber;//操作次数显示框
    public TextView Electricity;//电池电量显示框
    private TextView logindate;//最近登录日期显示框
    private ToggleButton loseSwitch;//防丢开关
    private ToggleButton lockSwitch;//锁定、解锁开关
    private ImageButton bt_connect;//连接蓝牙按钮
    private ImageButton bt_find;//找寻按钮
    private ImageButton bt_sqApply;//申请授权按钮
    private ToggleButton bt_start;//启动，关闭设备开关
    private String confirmPsd=null;//启动时的密码
    private String lala;//自定义常量，一般代表错误信息
    private String equID;//设备的ID
    private String st;//自定义变量，给设备发送授权次数时使用
    private String t3 = "0000";
    //private String[] mToastStyleDes;
    private int mToastStyle;
    private List<EquipmentInfo> mEquipmentInfo;//设备信息列表
    private List<String> equList = new ArrayList<String>();//设备列表
    private List<String> imgList = new ArrayList<String>();//广告轮播图片列表
    JSONArray arr = new JSONArray();//json数组
    private int count = 0;//操作次数
    private String useLocation;//使用时地理位置
    private Thread t5;//报警线程
    private List<RightInfo> mRightInfo;//授权记录列表
    private BluetoothLeService nBluetoothLeService;//蓝牙服务
    private BluetoothGattCharacteristic target_chara = null;//蓝牙传输关键字
    private Handler mhandler = new Handler();//消息处理者
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    //蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ID";//用户ID常量
    private static final String ARG_PARAM2 = "usertype";//用户类型常量
    AlertDialog.Builder builder;
    AlertDialog dialog;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();//地理位置监听者
    //消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                if(usertype.equals("9")) {
                    mRightInfo = ApplyService.mRightInfo;
                    RightInfo nRightInfo;
                    nRightInfo = mRightInfo.get(mRightInfo.size()-1);
                    Log.i("ddd","+++++"+nRightInfo.getSqNumber());
                    if(Integer.parseInt(nRightInfo.getSqNumber())<16){
                        String st1 = Integer.toHexString(Integer.parseInt(nRightInfo.getSqNumber()));
                        st = "F6F601030002000"+st1+"03EE16";
                    }else if(Integer.parseInt(nRightInfo.getSqNumber())>=16&&Integer.parseInt(nRightInfo.getSqNumber())<256){
                        String st1 = Integer.toHexString(Integer.parseInt(nRightInfo.getSqNumber()));
                        st = "F6F60103000200"+st1+"03EE16";
                    }else if(Integer.parseInt(nRightInfo.getSqNumber())>=256&&Integer.parseInt(nRightInfo.getSqNumber())<4096){
                        String st1 = Integer.toHexString(Integer.parseInt(nRightInfo.getSqNumber()));
                        st = "F6F6010300020"+st1+"03EE16";
                    }else if(Integer.parseInt(nRightInfo.getSqNumber())>=4096){
                        String st1 = Integer.toHexString(Integer.parseInt(nRightInfo.getSqNumber()));
                        st = "F6F601030002"+st1+"03EE16";
                    }

                }
                else{
                    st = "F6F601030002FF00FF0616";
                }
                byte[] bytes = Tobytes.hexStringToBytes(st);
                target_chara.setValue(bytes);
                //调用蓝牙服务的写特征值方法实现发送数据
                nBluetoothLeService.writeCharacteristic(target_chara);
//                String st1 = "F6F6010200025500000516";
//                byte[] bytes1 = Tobytes.hexStringToBytes(st1);
//                target_chara.setValue(bytes1);
//                //调用蓝牙服务的写特征值方法实现发送数据
//                nBluetoothLeService.writeCharacteristic(target_chara);
                dialog.dismiss();
            }else if(msg.what == 2){
                ToastUtil.show(getContext(), lala);
            }
            else if(msg.what==3){
                Log.i("lalala","lllll"+equList.size());
                //Log.i("lalala","lllll"+equList.get(2));
            }
            else if(msg.what==4){
                String[] images = (String[])imgList.toArray(new String[0]);
                //String[] images= ConstantValue.imgUrls;
                //String[] titles=new String[]{"标题"};
                banner = (Banner) getActivity().findViewById(R.id.banner1);
                //一步搞定，设置图片就行了
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                banner.setImages(images);
            }
            else if(msg.what==5){
                ToastUtil.show(getContext(),"设备关闭成功");
            }
        }
    };


    // TODO: Rename and change types of parameters
    private String ID;//用户ID
    private String usertype;//用户类型

    private OnFragmentInteractionListener mListener;

    public FirstPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Function.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstPage newInstance(String param1, String param2) {
        FirstPage fragment = new FirstPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID = getArguments().getString(ARG_PARAM1);
            usertype = getArguments().getString(ARG_PARAM2);
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter()
    {
        //意图适配
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //初始化广告轮播
        initBanner();
        //初始化控件
        initUI();
        //初始化百度地图接口
        initLocation();
        //实时监控电量
        //initelect();
        /* 启动蓝牙service */
        Intent gattServiceIntent = new Intent(getContext(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //初始化数据
        initData();
        //地理位置开启
        mLocationClient.start();
        //初始化媒体播放器
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm);
        //初始化音频管理器
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        //初始化音量
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //mediaPlayer.start();
        //报警线程
        t5 = new Thread(){
            @Override
            public void run() {
                mediaPlayer.start();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //t5.start();
    }

    private void initData() {
        //开启一个线程，获取设备列表
        Thread t2=new Thread(new MyRunnable5());
        t2.start();

        //mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
        //获取登录时间
        preferences = getActivity().getSharedPreferences("liushuai",getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        String time2 = preferences.getString("time",null);
//        SharedPreferences preferDataList1 = getActivity().getSharedPreferences("liushuai1", getActivity().MODE_PRIVATE);
//        if(preferDataList1!=null){
//        count = preferDataList1.getInt("count",0);
//        }
        sqNumber.setText("操作次数     "+count+"次");
        if(time2==null) {
            //记录第一次登录时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            editor.putString("time",str);
            editor.commit();
            logindate.setText(str);
        }else{
            //设置显示为上次登录时间，并记录本次登录时间
            logindate.setText(time2);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            editor.putString("time", str);
            editor.commit();
        }
    }

    private void initelect() {
        if (BluetoothLeService.conncetState) {
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //要做的事情
                    String st = "f6f6010500020000000816";
                    byte[] bytes = Tobytes.hexStringToBytes(st);
                    target_chara.setValue(bytes);
                    //调用蓝牙服务的写特征值方法实现发送数据
                    nBluetoothLeService.writeCharacteristic(target_chara);
                }
            };
            handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.
        }
    }

    private void initUI() {
        sqNumber = (TextView)getActivity().findViewById(R.id.sqNumber);
        Electricity = (TextView)getActivity().findViewById(R.id.Electricity);
        logindate = (TextView)getActivity().findViewById(R.id.logindate);
        //bt_connect = (Button)getActivity().findViewById(R.id.bt_connect);
        bt_connect = (ImageButton)getActivity().findViewById(R.id.bt_connect);
        bt_connect.setOnClickListener(this);
        bt_start = (ToggleButton)getActivity().findViewById(R.id.bt_start);
        //bt_start.setOnClickListener(this);
        //启动开关监听事件
        bt_start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //showConfirmPsdDialog();
//                String st = "F6F6010200025500000516";
//                byte[] bytes = Tobytes.hexStringToBytes(st);
//                target_chara.setValue(bytes);
//                //调用蓝牙服务的写特征值方法实现发送数据
//                nBluetoothLeService.writeCharacteristic(target_chara);
//                String st1 = "F6F601030002000503EE16";
//                byte[] bytes1 = Tobytes.hexStringToBytes(st1);
//                target_chara.setValue(bytes1);
//                //调用蓝牙服务的写特征值方法实现发送数据
//                nBluetoothLeService.writeCharacteristic(target_chara);
                    if(BluetoothLeService.conncetState){
                        if(ApplyService.mresult.equals("1")){
                            if(ApplyService.usestate.equals("2")){
                                ToastUtil.show(getContext(),"本次使用没有授权");
                                bt_start.setChecked(false);
                            }else{
                                //弹出输入密码对话框
                                showConfirmPsdDialog();
                            }
                        }else{
                            ToastUtil.show(getContext(),"该设备没有授权");
                            bt_start.setChecked(false);
                        }
                    }else{
                        ToastUtil.show(getContext(),"蓝牙未连接");
                        bt_start.setChecked(false);
                    }
                }else{
//                    String st = "F6F6010200025500000516";
//                    byte[] bytes = Tobytes.hexStringToBytes(st);
//                    target_chara.setValue(bytes);
//                    //调用蓝牙服务的写特征值方法实现发送数据
//                    nBluetoothLeService.writeCharacteristic(target_chara);
                    String st1 = "F6F6010100025500000416";
                    byte[] bytes1 = Tobytes.hexStringToBytes(st1);
                    target_chara.setValue(bytes1);
                    //调用蓝牙服务的写特征值方法实现发送数据
                    nBluetoothLeService.writeCharacteristic(target_chara);
                    Thread t=new Thread(new MyRunnable8());//这里比第一种创建线程对象多了个任务对象
                    t.start();
//                    st = "F6F601020002AA00000616";
//                    byte[] bytes = Tobytes.hexStringToBytes(st);
//                    target_chara.setValue(bytes);
//                    //调用蓝牙服务的写特征值方法实现发送数据
//                    nBluetoothLeService.writeCharacteristic(target_chara);
                }
            }
        });
        bt_sqApply = (ImageButton)getActivity().findViewById(R.id.bt_sqApply);
        bt_sqApply.setOnClickListener(this);
        bt_find = (ImageButton)getActivity().findViewById(R.id.bt_find);
        bt_find.setOnClickListener(this);
        loseSwitch = (ToggleButton)getActivity().findViewById(R.id.loseSwitch);
        loseSwitch.setChecked(true);
        //防丢开关监听事件
        loseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(BluetoothLeService.conncetState) {
                    if (isChecked) {
                        //System.out.println("lalalalalalalalalalalalallalal");
                        String st = "f6f6010400020001000816";
                        byte[] bytes = Tobytes.hexStringToBytes(st);
                        target_chara.setValue(bytes);
                        //调用蓝牙服务的写特征值方法实现发送数据
                        nBluetoothLeService.writeCharacteristic(target_chara);
                    } else {
                        String st = "f6f6010400020000000716";
                        byte[] bytes = Tobytes.hexStringToBytes(st);
                        target_chara.setValue(bytes);
                        //调用蓝牙服务的写特征值方法实现发送数据
                        nBluetoothLeService.writeCharacteristic(target_chara);
                    }
                }else {
                    loseSwitch.setChecked(false);
                    ToastUtil.show(getContext(),"蓝牙未连接");
                }

            }
        });
        //锁定解锁设备监听事件
        lockSwitch = (ToggleButton)getActivity().findViewById(R.id.lockSwitch);
        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(usertype.equals("9")||usertype.equals("3")){
                        ToastUtil.show(getContext(),"没有此权限");
                        lockSwitch.setChecked(false);
                    }else{
                        //弹出设备选项对话框
                        showToastStyleDialog1();
                    }
                }
                else{
                    //弹出设备选项对话框
                    showToastStyleDialog2();
                }
            }
        });
    }
    //初始化广告轮播
    private void initBanner() {
        //开启一个新线程，获取服务器图片
        Thread t=new Thread(new MyRunnable6());//这里比第一种创建线程对象多了个任务对象
        t.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_page, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_connect:
//                Log.i("mmm",useLocation);
                //进入连接蓝牙页面
                Intent intent = new Intent(getContext(),BlueList.class);
                startActivity(intent);
                break;
//            case R.id.bt_start:
//                //showConfirmPsdDialog();
//
//                if(BluetoothLeService.conncetState){
//                    if(ApplyService.mresult.equals("1")){
//                        showConfirmPsdDialog();
//                    }else{
//                        ToastUtil.show(getContext(),"该设备没有授权");
//                    }
//                }else{
//                    ToastUtil.show(getContext(),"蓝牙未连接");
//                }
                //showConfirmPsdDialog();
//                String st = "F6F601030002FF00FF0616";
//                byte[] bytes = Tobytes.hexStringToBytes(st);
//                target_chara.setValue(bytes);
//                //调用蓝牙服务的写特征值方法实现发送数据
//                nBluetoothLeService.writeCharacteristic(target_chara);
//                String st1 = "F6F601010002AA00000516";
//                byte[] bytes1 = Tobytes.hexStringToBytes(st1);
//                target_chara.setValue(bytes1);
//                //调用蓝牙服务的写特征值方法实现发送数据
//                nBluetoothLeService.writeCharacteristic(target_chara);
                //break;
            //如果是普通用户，进入授权页面，如果是管理员，直接授权成功
            case R.id.bt_sqApply:
                if(usertype.equals("9")) {
                    Intent intent4 = new Intent(getContext(), SqApply.class);
                    //用Bundle携带数据
                    Bundle bundle = new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("ID", ID);
                    bundle.putString("usertype", usertype);
                    intent4.putExtras(bundle);
                    startActivity(intent4);
                }else{
                    Thread t=new Thread(new MyRunnable7());//这里比第一种创建线程对象多了个任务对象
                    t.start();
                }
                break;
            //找寻设备，先判断蓝牙有没有连接，如果连接了直接给设备发送指令
            case R.id.bt_find:
                if(BluetoothLeService.conncetState) {
                    String st2 = "F6F6010400020002000916";
                    byte[] bytes2 = Tobytes.hexStringToBytes(st2);
                    target_chara.setValue(bytes2);
                    //调用蓝牙服务的写特征值方法实现发送数据
                    nBluetoothLeService.writeCharacteristic(target_chara);
                }else{
                    ToastUtil.show(getContext(), "蓝牙未连接");
                }
                break;
        }
    }

    private void showConfirmPsdDialog() {
        //view由自己编写的XML转换成view对象的xml
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        final View view = View.inflate(getActivity(),R.layout.dialog_corfim_psd,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_Psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                confirmPsd = et_confirm_Psd.getText().toString();
                Log.i("confirmpsd",confirmPsd);
                //启动一个新线程，验证密码是否正确
                Thread t=new Thread(new MyRunnable());//这里比第一种创建线程对象多了个任务对象
                t.start();
//                if(!TextUtils.isEmpty(confirmPsd)){
//                    String psd = SpUtil.getString(getContext(), "1234", "");
//                    if(psd.equals(Md5Util.encoder(confirmPsd))){
//                            String st1 = "F6F601010002AA00000516";
//                            byte[] bytes1 = Tobytes.hexStringToBytes(st1);
//                            target_chara.setValue(bytes1);
//                            //调用蓝牙服务的写特征值方法实现发送数据
//                            nBluetoothLeService.writeCharacteristic(target_chara);
//                            //隐藏对话框
//                            dialog.dismiss();
//                            SpUtil.putString(getContext(), ConstantValue.MOBILE_SAFE_PSD, psd);
//                    }
//                    else{
//                        ToastUtil.show(getContext(),"确认密码错误");
//                    }
//                }
//                else {
//                    //提示用户当前密码为空
//                    ToastUtil.show(getContext(),"请输入密码");
//                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void onResume()
    {
        super.onResume();
        //绑定广播接收器
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (BlueList.mBluetoothLeService != null)
//        {
//            //根据蓝牙地址，建立连接
//            final boolean result = BlueList.mBluetoothLeService.connect(BlueList.mDeviceAddress);
//            Log.d("lalalalalla", "Connect request result=" + result);
//        }
    }
    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {
                ToastUtil.show(getContext(),"连接成功");
                //mConnected = true;
                //status = "connected";
                //更新连接状态
                //updateConnectionState(status);
                //mediaPlayer.stop();
                //t5.stop();
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action))
            {
                ToastUtil.show(getContext(),"连接失败");
                //mConnected = false;
                //status = "disconnected";
                //更新连接状态
                //updateConnectionState(status);
                //t5.start();
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
                    .equals(action))
            {
                // Show all the supported services and characteristics on the
                // user interface.
                //获取设备的所有蓝牙服务
                displayGattServices(nBluetoothLeService
                        .getSupportedGattServices());
                System.out.println("BroadcastReceiver :"
                        + "device SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
            {
                //处理发送过来的数据
                displayData(intent.getExtras().getString(
                        BluetoothLeService.EXTRA_DATA));
//                String result = intent.getExtras().getString(
//                        BluetoothLeService.EXTRA_DATA);
                System.out.println("BroadcastReceiver onData:"
                        + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices)
    {

        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";

        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // 特征数据（隶属于某一级服务下面的特征值集合）
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        // 部分层次，所有特征值集合
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices)
        {

            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

            gattServiceData.add(currentServiceData);

            System.out.println("Service uuid:" + uuid);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString()
                        .equals(HEART_RATE_MEASUREMENT))
                {
                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mhandler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            // TODO Auto-generated method stub
                            nBluetoothLeService
                                    .readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    nBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    nBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors)
                {
                    System.out.println("---descriptor UUID:"
                            + descriptor.getUuid());
                    // 获取特征值的描述
                    nBluetoothLeService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }

                gattCharacteristicGroupData.add(currentCharaData);
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
            mGattCharacteristics.add(charas);
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }

    }

    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service)
        {
            nBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!nBluetoothLeService.initialize())
            {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                //finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            //nBluetoothLeService.connect(mDeviceAddress);
        }
        public void onServiceDisconnected(ComponentName componentName)
        {
            nBluetoothLeService = null;
        }

    };
    //处理接收到的硬件设备的信息
    private void displayData(final String rev_string)
    {
        //Electricity.setText(rev_string);
        if(rev_string.length()>8) {
            //截取前8个字符，判断数据为什么类型的数据
            String s1 = rev_string.substring(0, 8);
            System.out.println(s1 + "lalalalalalallalalalalalalalalalal");
            if (s1.equals("a6a60106")) {
                String st1 = "F6F601060002AA00000816";
                byte[] bytes1 = Tobytes.hexStringToBytes(st1);
                target_chara.setValue(bytes1);
                //调用蓝牙服务的写特征值方法实现发送数据
                nBluetoothLeService.writeCharacteristic(target_chara);
                //y1为BatH,y2为BatL,y3为RefH,y4为RefL
                int y1 = Integer.parseInt(rev_string.substring(12,14),16);
                int y2 = Integer.parseInt(rev_string.substring(14,16),16);
                int y3 = Integer.parseInt(rev_string.substring(16,18),16);
                int y4 = Integer.parseInt(rev_string.substring(18,20),16);
                double vbat = 8*(y1*256+y2)*1.0/(y3*256+y4);
                if(vbat>3.6){
                    Electricity.setText("100%");
                }else if(vbat>=2.7&&vbat<=3.6){
                    Electricity.setText(((vbat-2.7)*100+10)+"%");
                }else if(vbat>2.5&&vbat<2.7){
                    Electricity.setText((vbat-2.5)*50+"%");
                }else {
                    Electricity.setText("0%");
                }
//                String t2 = rev_string.substring(16,20);
//                Log.i("ccc","t2="+t2);
//                if(!t2.equals("0000")) {
//                    if(!t2.equals(t3)) {
//                        Thread t = new Thread(new MyRunnable3());//这里比第一种创建线程对象多了个任务对象
//                        t.start();
//                        count++;
//                        t3 = t2;
//                    }
////                    preferences = getActivity().getSharedPreferences("liushuai1", getActivity().MODE_PRIVATE);
////                    editor = preferences.edit();
////                    editor.putInt("count", count);
////                    //editor.putString("count", "" + count);
////                    editor.commit();
//                    sqNumber.setText("操作次数     " + count + "次");
//                }

            }
            else if(s1.equals("a6a60103")){
                //开启一个新线程，上传使用日志
                Thread t = new Thread(new MyRunnable3());//这里比第一种创建线程对象多了个任务对象
                t.start();
                count++;
                sqNumber.setText("操作次数     "+(count-1)+"次");
            }
        }
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Electricity.setText(rev_string);
//            }
//        });

    }
    //启动时验证密码函数
    public String AddUser() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "Start";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("StartPwd",confirmPsd);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
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
                lala = AddUser();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //锁定设备函数
    public String Lock() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "LockEqu";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId", equID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
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
                lala = Lock();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //解锁设备函数
    public String Unlock() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "UnLockEqu";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId", equID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
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
                lala = Unlock();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //上传设备使用日志函数
    public String AdduseLog() {
        //useLocation = MyLocationListener.loc.get;
        mRightInfo = ApplyService.mRightInfo;
        RightInfo nRightInfo;
        nRightInfo = mRightInfo.get(mRightInfo.size()-1);
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "AddEquUseLog";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("authorizedId",nRightInfo.getSqCode());
        request.addProperty("equId",nRightInfo.getEquipmentID());
        request.addProperty("useLocation",useLocation);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }
    public class MyRunnable3 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                lala = AdduseLog();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //初始化位置
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
    //选择设备弹框
    private void showToastStyleDialog1() {
        final String[] mToastStyleDes = (String[])equList.toArray(new String[0]);
        Log.i("lalala", "shuzuchangdi" + mToastStyleDes.length);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择设备");
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//which选中的索引值
                dialog.dismiss();
                for (int i = 0; i < mEquipmentInfo.size(); i++) {
                    if (mToastStyleDes[which].equals(mEquipmentInfo.get(i).getEquipmentName())) {
                        equID = mEquipmentInfo.get(i).getEquipmentID();
                    }
                }
                Thread t = new Thread(new MyRunnable1());//这里比第一种创建线程对象多了个任务对象
                t.start();
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //选择设备弹框
    private void showToastStyleDialog2() {
        final String[] mToastStyleDes = (String[])equList.toArray(new String[0]);
        Log.i("lalala","shuzuchangdi"+mToastStyleDes.length);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择设备");
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//which选中的索引值
                dialog.dismiss();
                for(int i = 0;i < mEquipmentInfo.size();i++){
                    if(mToastStyleDes[which].equals(mEquipmentInfo.get(i).getEquipmentName())){
                        equID = mEquipmentInfo.get(i).getEquipmentID();
                    }
                }
                Thread t=new Thread(new MyRunnable2());//这里比第一种创建线程对象多了个任务对象
                t.start();
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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

    public class MyRunnable5 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                arr = Getepu();
                Log.i("lalala", "" + "length" + arr.length());
                mEquipmentInfo = new ArrayList<EquipmentInfo>();
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
            Log.i("lalala", "siuze " + mEquipmentInfo.size());
            handler.sendEmptyMessage(3);
        }
    }
    //获取图片函数
    public JSONArray GetImg() {
        Log.i("lalala", ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "GetListImage";
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
    public class MyRunnable6 implements Runnable {
        public void run() {
            //你需要实现的代码
            arr = GetImg();
            for (int i = 0; i < arr.length(); i++){
                try {
                    JSONObject temp = (JSONObject) arr.get(i);
                    imgList.add(temp.getString("bContent"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(4);
        }
    }
    //管理员申请使用的函数
    public String Applyadmin() {
        Log.i("lalala",ID);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "ApplyAdmin";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("equId","1");
        request.addProperty("applyTimes","100");
        request.addProperty("reason","管理员使用");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }

    public class MyRunnable7 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                lala = Applyadmin();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(1);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //设备使用完成后结束使用函数
    public String EndApply() {
        Log.i("lalala",ID);
        mRightInfo = ApplyService.mRightInfo;
        RightInfo nRightInfo;
        nRightInfo = mRightInfo.get(mRightInfo.size()-1);
        String url = "http://59.48.235.234:8010/DeviceManageService.asmx";
        String nameSpace = "http://wgkj.com/";
        //String SOAP_ACTION  = "http:/xx.xx.com/services/User/login";
        String method = "EndApply";
        SoapObject request = new SoapObject(nameSpace, method);

        request.addProperty("PhoneId", ID);
        request.addProperty("authorizedId",nRightInfo.getSqCode());
        request.addProperty("equId",nRightInfo.getEquipmentID());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        HttpTransportSE ht = new HttpTransportSE(url);
        ht.debug = true;
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //new MarshalDouble().register(envelope);
        String jieguo=null;
        try {
            ht.call(nameSpace+method, envelope);
            // 获取服务器响应返回的SOAP消息
            Object result = (Object)envelope.getResponse();
            String aaa = result.toString();
            JSONObject bbb = new JSONObject(aaa);
            jieguo = bbb.getString("errmsg");
        } catch (Exception e) {
            Log.i("lalala", "不成功" + jieguo);
            e.printStackTrace();
            String ee = e.getMessage();
            Log.e("eee",ee);
        }
        return jieguo;
    }

    public class MyRunnable8 implements Runnable {
        public void run() {
            //你需要实现的代码
            //String lala = null;
            try {
                lala = EndApply();
                if(!lala.equals("")){
                    handler.sendEmptyMessage(5);
                }else {
                    handler.sendEmptyMessage(2);
                }
                Log.i("lalala", lala);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //地理位置监听
    public class MyLocationListener implements BDLocationListener {
        public String loc;
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                useLocation = location.getAddrStr();
                System.out.println("nnnn"+useLocation);
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append(location.getAddress());
                Log.i("lalala",location.getAddress().toString());
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //Log.i("BaiduLocationApiDem", sb.toString());
        }
    }


}
