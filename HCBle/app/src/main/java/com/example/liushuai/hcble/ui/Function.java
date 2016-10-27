package com.example.liushuai.hcble.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.liushuai.hcble.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Function.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Function#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Function extends Fragment {
    private LinearLayout bt_yonghu;//用户列表
    private LinearLayout bt_equiplist;//设备列表
    private LinearLayout bt_rightRecord;//授权记录
    private LinearLayout bt_useRecord;//使用记录
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ID";
    private static final String ARG_PARAM2 = "usertype";

    // TODO: Rename and change types of parameters
    private String ID;
    private String usertype;

    private OnFragmentInteractionListener mListener;

    public Function() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Function.
     */
    // TODO: Rename and change types and number of parameters
    public static Function newInstance(String param1, String param2) {
        Function fragment = new Function();
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
        //用户管理
        inityonghu();
        //设备列表
        initequip();
        //授权记录
        initright();
        //使用记录
        inituse();
    }

    private void inituse() {
        bt_useRecord = (LinearLayout)getActivity().findViewById(R.id.bt_useRecord);
        bt_useRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UseRecord.class);
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initright() {
        bt_rightRecord = (LinearLayout)getActivity().findViewById(R.id.bt_rightRecord);
        bt_rightRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),RightRecord.class);
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                bundle.putString("tz","1");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initequip() {
        bt_equiplist = (LinearLayout)getActivity().findViewById(R.id.bt_equiplist);
        bt_equiplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Equiplist.class);
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void inityonghu() {
        bt_yonghu = (LinearLayout)getActivity().findViewById(R.id.bt_yonghu);
        if(usertype.equals("9")){
            bt_yonghu.setEnabled(false);
        }
        bt_yonghu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Yonghu.class);
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype", usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_function, container, false);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
