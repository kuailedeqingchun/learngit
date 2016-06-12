package com.example.liushuai.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by liushuai on 2016/5/23.
 */
public class ServiceUtil {
    public static boolean isRunning(Context ctx,String serviceName){
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取手机中正在运行的服务集合
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = mAM.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos){
            if(serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
