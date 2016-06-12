package com.example.liushuai.mobilesafe.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.liushuai.mobilesafe.R;
import com.example.liushuai.mobilesafe.db.dao.BlackNumberDao;
import com.example.liushuai.mobilesafe.db.domain.BlackNumberInfo;
import com.example.liushuai.mobilesafe.utils.ToastUtil;

import java.util.List;

public class BlackNumberActivity extends AppCompatActivity {

    private Button bt_add;
    private ListView lv_blacknumber;
    private int mode = 1;
    private int mCount;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private MyAdapter mAdapter;
    private boolean mIsLoad = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mAdapter == null){
                mAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(mAdapter);
            }
            else{
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_black_number);
        initUI();
        initData();
    }

    private void initData() {
        //获取数据库中所有电话号码
        new Thread(){
            @Override
            public void run() {
                //获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //查询部分数据
                mBlackNumberList = mDao.find(0);
                mCount = mDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add = (Button)findViewById(R.id.bt_add);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //监听其滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mBlackNumberList!=null){
                    //条件一:滚动到停止状态
                    //条件二:最后一个条目可见
                    if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
                            && !mIsLoad){
                        if(mCount>mBlackNumberList.size()) {
                            new Thread() {
                                @Override
                                public void run() {
                                    //1,获取操作黑名单数据库的对象
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //2,查询部分数据
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                                    //3,添加下一页数据的过程
                                    mBlackNumberList.addAll(moreData);
                                    //4,通知数据适配器刷新
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view,0,0,0,0);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    mDao.insert(phone, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    //将对象插到集合中
                    mBlackNumberList.add(0, blackNumberInfo);
                    //通知数据适配器刷新
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    //隐藏对话框
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //View view = null;
           ViewHolder holder = null;
            if(convertView == null){
                convertView= View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                //减少findViewByid的次数
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView)convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView)convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1,数据库删除
                    mDao.delete(mBlackNumberList.get(position).phone);
                    //2,集合中的删除
                    mBlackNumberList.remove(position);
                    //3,通知数据适配器刷新
                    if(mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            holder.tv_phone.setText(mBlackNumberList.get(position).phone);
            int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode){
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }
    //复用viewHolder步骤二
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

}
