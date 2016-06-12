package com.example.liushuai.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //2,创建手势管理对象
        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getX()-e2.getX()>0){
                    showNextPage();
                }
                if(e1.getX()-e2.getX()<0){
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3,通过手势处理类，处理事件
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //下一页的抽象方法,由子类决定具体跳转到那个界面
    protected abstract void showNextPage();
    //上一页的抽象方法,由子类决定具体跳转到那个界面
    protected abstract void showPrePage();


    //点击下一页按钮的时候,根据子类的showNextPage方法做相应跳转
    public void nextPage(View view){
        showNextPage();
    }

    //点击上一页按钮的时候,根据子类的showPrePage方法做相应跳转
    public void prePage(View view){
        showPrePage();
    }
}
