package com.example.liushuai.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;

/**
 * Created by liushuai on 2016/5/15.
 */
public class SettingItemView extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.liushuai.mobilesafe";
    private static final String tag = "SettingItemView";
    private CheckBox cb_box;
    private TextView tv_des;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;
    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public SettingItemView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.setting_item_view,this);
        //自定义组合控件中的标题描述
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        //获取自定义以及原生属性的操作,写在此处,AttributeSet attrs对象中获取
        initAttrs(attrs);
        //获取布局文件中定义的字符串,赋值给自定义组合控件的标题
        tv_title.setText(mDestitle);
    }

    private void initAttrs(AttributeSet attrs) {
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

        Log.i(tag, mDestitle);
        Log.i(tag, mDesoff);
        Log.i(tag, mDeson);
    }
    //判断是否开启的方法
    public boolean isCheck(){
        return cb_box.isChecked();
    }
    public void setCheck(boolean isCheck){
        cb_box.setChecked(isCheck);
        if(isCheck){
            //开启
            tv_des.setText(mDeson);
        }
        else{
            //关闭
            tv_des.setText(mDesoff);
        }
    }
}
