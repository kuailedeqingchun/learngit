package com.example.liushuai.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by liushuai on 2016/5/15.
 */
public class FocusTextView extends TextView {
    //使用在通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    //由系统调用(带属性+上下文环境构造方法)
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写获取焦点的方法，调用时默认获取焦点

    @Override
    public boolean isFocused() {
        return true;
    }
}
