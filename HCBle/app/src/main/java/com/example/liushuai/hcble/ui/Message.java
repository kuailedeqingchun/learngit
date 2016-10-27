package com.example.liushuai.hcble.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liushuai.hcble.R;
import com.example.liushuai.hcble.engine.MessageProvider;
import com.example.liushuai.hcble.model.EquipmentInfo;
import com.example.liushuai.hcble.model.MessageInfo;
import com.example.liushuai.hcble.service.MessageService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Message.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Message#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Message extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListView lv;
    private int count=0;
    private MyAdapter mAdapter;
    public static List<MessageInfo> mMessageInfo;
    public static List<MessageInfo> nMessageInfo = new ArrayList<MessageInfo>();
    public static List<String> ydmessage = new ArrayList<String>();
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new MyAdapter();
            lv.setAdapter(mAdapter);

        };
    };

    private static final String ARG_PARAM1 = "ID";
    private static final String ARG_PARAM2 = "usertype";

    // TODO: Rename and change types of parameters
    private String ID;
    private String usertype;
    /**
     * 用来与外部activity交互的
     */

    private FragmentInteraction listterner;
    private OnFragmentInteractionListener mListener;

    public Message() {
        // Required empty public constructor
    }
    /**
     * 定义了所有activity必须实现的接口
     */
    public interface FragmentInteraction
    {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void updata(String str);


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Message.
     */
    // TODO: Rename and change types and number of parameters
    public static Message newInstance(String param1, String param2) {
        Message fragment = new Message();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID = getArguments().getString(ARG_PARAM1);
            usertype = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取数据
        getData();
        //初始化UI
        initUI();
    }

    private void getData() {
        mMessageInfo = MessageService.mMessageInfo;
        nMessageInfo.clear();
        if(mMessageInfo.size()>=1) {
            for (int i = mMessageInfo.size() - 1; i >= 0; i--) {
                nMessageInfo.add(mMessageInfo.get(i));
            }
            mHandler.sendEmptyMessage(0);
        }
    }

    private void initUI() {
        lv = (ListView)getActivity().findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ydmessage.add(mAdapter.getItem(position).getMsgId());
                MessageService.environNums++;
                SharedPreferences.Editor editor1 = getActivity().getSharedPreferences("EnvironDataList", getActivity().MODE_PRIVATE).edit();
                editor1.putInt("EnvironNums", MessageService.environNums);
                editor1.commit();
                SharedPreferences.Editor editor2 = getActivity().getSharedPreferences("EnvironDataList2", getActivity().MODE_PRIVATE).edit();
                editor2.putString(""+MessageService.environNums, mAdapter.getItem(position).getMsgId());
                editor2.commit();
                //listterner.updata("6");
                //判断消息类型
                if(mAdapter.getItem(position).getMsgType().equals("1")){
                    Intent intent = new Intent(getContext(),ShouQMessage.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("ID", ID);
                    bundle.putString("usertype", usertype);
                    bundle.putString("msgId",mAdapter.getItem(position).getMsgId());
                    bundle.putString("msgBody",mAdapter.getItem(position).getMessageBody());
                    bundle.putString("KeyId",mAdapter.getItem(position).getKeyId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(),SystemMessage.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("ID", ID);
                    bundle.putString("usertype", usertype);
                    bundle.putString("msgId",mAdapter.getItem(position).getMsgId());
                    bundle.putString("msgBody",mAdapter.getItem(position).getMessageBody());
                    bundle.putString("KeyId",mAdapter.getItem(position).getKeyId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nMessageInfo.size();
        }

        @Override
        public MessageInfo getItem(int position) {
            return nMessageInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.listview_message, null);
                holder = new ViewHolder();
                holder.messageTitle = (TextView)convertView.findViewById(R.id.messageTitle);
                holder.messageBody = (TextView)convertView.findViewById(R.id.messageBody);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.messageTitle.setText(getItem(position).messageTitle);
            holder.messageBody.setText(getItem(position).messageBody);

            return convertView;
        }
    }
    static class ViewHolder{
        TextView messageTitle;
        TextView messageBody;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if(context instanceof FragmentInteraction)
        {
            listterner = (FragmentInteraction)context;
        }
        else{
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        listterner = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
