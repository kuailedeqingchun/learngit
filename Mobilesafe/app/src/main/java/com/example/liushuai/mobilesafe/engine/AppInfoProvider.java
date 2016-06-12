package com.example.liushuai.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.liushuai.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuai on 2016/5/27.
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context ctx){
        PackageManager pm = ctx.getPackageManager();
        //获取安装在手机上应用信息的集合
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        //循环遍历
        for(PackageInfo packageInfo : packageInfoList){
            AppInfo appInfo = new AppInfo();
            //获取应用的包名
            appInfo.packageName = packageInfo.packageName;
            //应用名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(pm).toString();
            //获取图标
            appInfo.icon = applicationInfo.loadIcon(pm);
            //7,判断是否为系统应用(每一个手机上的应用对应的flag都不一致)
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.isSystem = true;
            }else{
                //非系统应用
                appInfo.isSystem = false;
            }
            //8,是否为sd卡中安装应用
            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                //系统应用
                appInfo.isSdCard = true;
            }else{
                //非系统应用
                appInfo.isSdCard = false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
