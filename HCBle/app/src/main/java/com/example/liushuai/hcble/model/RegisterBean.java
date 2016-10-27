package com.example.liushuai.hcble.model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by liushuai on 2016/9/26.
 */
public class RegisterBean implements KvmSerializable {
    public String PhoneId;//用户ID
    public String CheckCode;//验证码
    public String LoginPwd;//登录密码
    public String StartPwd;//启动密码

    public RegisterBean() {

    }

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return PhoneId;
            case 1:
                return CheckCode;
            case 2:
                return LoginPwd;
            case 3:
                return StartPwd;

        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                PhoneId = o.toString();
            case 1:
                CheckCode = o.toString();
            case 2:
                LoginPwd = o.toString();
            case 3:
                StartPwd = o.toString();
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "PhoneId";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "CheckCode";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "LoginPwd";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "StartPwd";
                break;
            default:
                break;
        }
    }
}
