package com.example.liushuai.hcble.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liushuai.hcble.R;

public class SqMessage extends BaseActionBarActivity {
    private TextView person;
    private Spinner spinner;
    private TextView phonenumber;
    private TextView number;
    private TextView sqreason;
    private Button submit;
    private Button cancelsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sq_message);
        getSupportActionBar().setTitle("授权消息");
        init();
    }

    private void init() {
        spinner = (Spinner)findViewById(R.id.spinner);
        person = (TextView)findViewById(R.id.person);
        phonenumber = (TextView)findViewById(R.id.phonenumber);
        number = (TextView)findViewById(R.id.number);
        sqreason = (TextView)findViewById(R.id.sqreason);
        submit = (Button)findViewById(R.id.submit);
        cancelsubmit = (Button)findViewById(R.id.cancelsubmit);
    }

}
