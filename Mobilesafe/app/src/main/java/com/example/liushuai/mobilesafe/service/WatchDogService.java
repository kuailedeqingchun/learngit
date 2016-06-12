package com.example.liushuai.mobilesafe.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import com.example.liushuai.mobilesafe.activity.EnterPsdActivity;
import com.example.liushuai.mobilesafe.db.dao.AppLockDao;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WatchDogService extends Service {
    private String mSkipPackagename;
    private AppLockDao mDao;
    private List<String> mPacknameList;
    private InnerReceiver mInnerReceiver;
    private boolean isWatch;
    private MyContentObserver mContentObserver;
    public WatchDogService() {
    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取包名
            mSkipPackagename = intent.getStringExtra("packagename");
        }
    }

    class MyContentObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    mPacknameList = mDao.findAll();
                    super.run();
                }
            }.start();
            super.onChange(selfChange);
        }
    }

    @Override
    public void onCreate() {
        System.out.println("服务开启了。。。。。。。");
        mDao = AppLockDao.getInstance(this);
        isWatch = true;
        watch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);

        mContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    private void watch(){
        new Thread(){
            @Override
            public void run() {
                mPacknameList = mDao.findAll();
                while (isWatch){
                    ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//                    //获取正在开启应用的任务栈
//                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
//                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
//                    //获取栈顶的Activity，并获取包名
                    //String packagename = runningTaskInfo.topActivity.getPackageName();
                    String packagename =getTopActivty();
                    //System.out.println(packagename+"。。。。。。。");
                    if(mPacknameList.contains(packagename)){
                        //如果解锁了，则不需要拦截
                        if(!packagename.equals(mSkipPackagename)){
                            System.out.println("需要拦截。。。。。。。");
                            //弹出拦截界面
                            Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename", packagename);
                            startActivity(intent);
                        }
                    }
                    try{
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        isWatch = false;
        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        if(mContentObserver!=null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }

    public String getTopActivty() {

        String topPackageName = "888";

//android5.0以上获取方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //startActivity(intent);


            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();

            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    //Log.e("TopPackage Name", topPackageName);
                }
            }
        }
        return topPackageName;
    }
}
