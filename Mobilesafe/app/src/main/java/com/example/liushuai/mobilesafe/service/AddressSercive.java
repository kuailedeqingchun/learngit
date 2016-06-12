package com.example.liushuai.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.engine.AddressDao;
import com.example.liushuai.mobilesafe.utils.ConstantValue;
import com.example.liushuai.mobilesafe.utils.SpUtil;

public class AddressSercive extends Service {
    public static final String tag = "AddressService";
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private String mAddress;
    private TextView tv_toast;
    private int[] mDrawableIds;
    private int mScreenHeight = mWM.getDefaultDisplay().getHeight();
    private int mScreenWidth = mWM.getDefaultDisplay().getWidth();
    public AddressSercive() {
    }

    class InnerOutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = getResultData();
            showToast(phone);
        }
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            tv_toast.setText(mAddress);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态，移除吐司
                    if(mWM!=null && mViewToast!=null){
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(tag, "响铃了.......................");
                    //响铃（展示吐司）
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE    默认能够被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.gravity = Gravity.LEFT+Gravity.TOP;
        //设定吐司显示效果
        mViewToast = View.inflate(this, R.layout.toast_view,null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX-startX;
                        int disY = moveY-startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                        //容错处理
                        if(params.x<0){
                            params.x = 0;
                        }

                        if(params.y<0){
                            params.y=0;
                        }

                        if(params.x>mScreenWidth-mViewToast.getWidth()){
                            params.x = mScreenWidth-mViewToast.getWidth();
                        }

                        if(params.y>mScreenHeight-mViewToast.getHeight()-22){
                            params.y = mScreenHeight-mViewToast.getHeight()-22;
                        }

                        //告知窗体吐司需要按照手势的移动,去做位置的更新
                        mWM.updateViewLayout(mViewToast, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_X, params.x);
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_Y, params.y);
                        break;
                }
                return true;
            }
        });

        //读取sp中存储吐司位置的x,y坐标值

        // params.x为吐司左上角的x的坐标
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        // params.y为吐司左上角的y的坐标
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
        mDrawableIds = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
        //在窗体上挂一个view（权限）
        mWM.addView(mViewToast,params);
        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onCreate() {
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        //监听播出电话的广播过滤条件(权限)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接受者
        InnerOutCallReceiver mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver, intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消对电话状态的监听(开启服务的时候监听电话的对象)
        if(mTM!=null && mPhoneStateListener!=null){
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }
}
