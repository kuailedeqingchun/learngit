package com.example.liushuai.mobilesafe.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

import com.example.liushuai.mobilesafe.R;

public class BaseCacheClearActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_base_cache_clear);
        //生成选项卡
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_cache_clear").setIndicator("sd卡清理");
        //告知点钟选项卡后续操作
        tab1.setContent(new Intent(this,CacheClearActivity.class));
        tab2.setContent(new Intent(this,SDCacheClearActivity.class));
        //将此两个选项卡维护host(选项卡宿主)中去
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }

}
