package com.example.liushuai.hcble.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.service.BluetoothLeService;
import com.example.liushuai.hcble.utils.ToastUtil;
import com.example.liushuai.hcble.utils.Tobytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlueList extends AppCompatActivity {
    private com.kyleduo.switchbutton.SwitchButton blueswitch;
    // 蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    // 蓝牙信号强度
    private ArrayList<Integer> rssis;
    // 自定义Adapter
    LeDeviceListAdapter mleDeviceListAdapter;
    // listview显示扫描到的蓝牙信息
    private ListView lv;
    // 描述扫描蓝牙的状态
   // private boolean mScanning;
    //private boolean scan_flag;
    private Handler nHandler;
    int REQUEST_ENABLE_BT = 1;
    // 蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;
    //蓝牙名字
    private String mDeviceName;
    //蓝牙地址
    public static String mDeviceAddress;
    //蓝牙信号值
    private String mRssi;
    //蓝牙service,负责后台的蓝牙服务
    private BluetoothLeService mBluetoothLeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_blue_list);
        //getSupportActionBar().setTitle("蓝牙列表");
        // 初始化控件
        init();
        // 初始化蓝牙
        init_ble();
        //扫描蓝牙
        scan();
        //scan_flag = true;
        // 自定义适配器
        //mleDeviceListAdapter = new LeDeviceListAdapter();

    }

    private void scan() {
        mleDeviceListAdapter = new LeDeviceListAdapter();
        lv.setAdapter(mleDeviceListAdapter);
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
// 注册广播接收器，接收并处理搜索结果
        registerReceiver(receiver, intentFilter);
// 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        if (enable)
        {
            // Stops scanning after a pre-defined scan period.
            nHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mScanning = false;
                    //scan_flag = true;
                    //scan_btn.setText("扫描设备");
                    Log.i("SCAN", "stop.....................");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
            Log.i("SCAN", "begin.....................");
            //mScanning = true;
            //scan_flag = false;
            //scan_btn.setText("停止扫描");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else
        {
            Log.i("Stop", "stoping................");
            //mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            //scan_flag = true;
        }
    }

    private void init_ble() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void init() {
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv = (ListView) this.findViewById(R.id.lv);
        // 为listview指定适配器
        lv.setAdapter(mleDeviceListAdapter);
        nHandler = new Handler();
        blueswitch = (com.kyleduo.switchbutton.SwitchButton)findViewById(R.id.blueswitch);
        blueswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    init();
                    // 初始化蓝牙
                    init_ble();
                    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                    scan();

//                    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(
//                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                        //绑定广播接收器
//                        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//                        if (mBluetoothLeService != null)
//                        {
//                            //根据蓝牙地址，建立连接
//                            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//                            Log.d("lalalalal", "Connect request result=" + result);
//                        }
//                        scan();
//                        mleDeviceListAdapter.notifyDataSetChanged();
//                    }
                } else {
                    System.out.println("关闭关闭关闭不帮你不安比关闭");
                    //解除广播接收器
                    unregisterReceiver(mGattUpdateReceiver);
                    mBluetoothLeService = null;
                    mBluetoothAdapter.disable();
                    mleDeviceListAdapter.clear();
//                    //mleDeviceListAdapter = null;
                    mleDeviceListAdapter.notifyDataSetChanged();
                }
            }
        });

        /* listview点击函数 */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                System.out.println("一点击低级点击点击电动机基地是粉丝");
                // TODO Auto-generated method stub
                final BluetoothDevice device = mleDeviceListAdapter
                        .getDevice(position);
                if (device == null)
                    return;
                mDeviceName = device.getName();
                mDeviceAddress = device.getAddress();
                mRssi = rssis.get(position).toString();
                System.out.println(mDeviceAddress+"lalajfskgjskghsk");

//                if (mScanning) {
//					/* 停止扫描设备 */
//                    mBluetoothAdapter.cancelDiscovery();
//                    //mScanning = false;
//                }
                /* 启动蓝牙service */
                Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                System.out.println(55555555 + "lalajfskgjskghsk");

            }
        });
    }

    protected void onResume()
    {
        super.onResume();
        //绑定广播接收器
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        scan();
        mleDeviceListAdapter.notifyDataSetChanged();
        if (mBluetoothLeService != null)
        {
            //根据蓝牙地址，建立连接
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("lalalalalla", "Connect request result=" + result);
        }
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
                ToastUtil.show(getApplicationContext(),"连接成功");
                //mConnected = true;
                //status = "connected";
                //更新连接状态
                //updateConnectionState(status);
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action))
            {
                ToastUtil.show(getApplicationContext(),"连接失败");
                //mConnected = false;
                //status = "disconnected";
                //更新连接状态
                //updateConnectionState(status);
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");

            }
        }
    };

    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service)
        {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize())
            {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            mBluetoothLeService.connect(mDeviceAddress);
        }
        public void onServiceDisconnected(ComponentName componentName)
        {
            mBluetoothLeService = null;
        }

    };

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter()
        {
            super();
            rssis = new ArrayList<Integer>();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi)
        {
            if (!mLeDevices.contains(device))
            {
                mLeDevices.add(device);
                rssis.add(rssi);
            }
        }

        public BluetoothDevice getDevice(int position)
        {
            return mLeDevices.get(position);
        }

        public void clear()
        {
            mLeDevices.clear();
            rssis.clear();
        }

        @Override
        public int getCount()
        {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i)
        {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        /**
         * 重写getview
         *
         * **/
        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {

            // General ListView optimization code.
            // 加载listview每一项的视图
            view = mInflator.inflate(R.layout.listitem, null);
            // 初始化三个textview显示蓝牙信息
            TextView deviceAddress = (TextView) view
                    .findViewById(R.id.tv_deviceAddr);
            TextView deviceName = (TextView) view
                    .findViewById(R.id.tv_deviceName);
            TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);

            BluetoothDevice device = mLeDevices.get(i);
            deviceAddress.setText(device.getAddress());
            deviceName.setText(device.getName());
            rssi.setText("" + rssis.get(i));

            return view;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                /* 讲扫描到设备的信息输出到listview的适配器 */
                //信号强度。
                short rssi = intent.getExtras().getShort(
                        BluetoothDevice.EXTRA_RSSI);
                mleDeviceListAdapter.addDevice(device, rssi);
                mleDeviceListAdapter.notifyDataSetChanged();
                //Log.i("SCAN", rssi);
                Log.i("SCAN", device.getName());
            }
        }
    };

    /**
     * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
     *
     * **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord)
        {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
					/* 讲扫描到设备的信息输出到listview的适配器 */
                    mleDeviceListAdapter.addDevice(device, rssi);
                    mleDeviceListAdapter.notifyDataSetChanged();
                }
            });

            System.out.println("Address:" + device.getAddress());
            System.out.println("Name:" + device.getName());
            System.out.println("rssi:" + rssi);

        }
    };


}
