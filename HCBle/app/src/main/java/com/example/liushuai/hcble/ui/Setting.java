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
 * {@link Setting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Setting extends Fragment {
    private LinearLayout userInformation;//个人信息
    private LinearLayout modifypassword;//修改登录密码
    private LinearLayout modifyczpassword;//修改启动密码
    private LinearLayout versioninformation;//版本信息
    private LinearLayout connectus;//联系我们
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ID";
    private static final String ARG_PARAM2 = "usertype";

    // TODO: Rename and change types of parameters
    private String ID;
    private String usertype;

    private OnFragmentInteractionListener mListener;

    public Setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Setting.
     */
    // TODO: Rename and change types and number of parameters
    public static Setting newInstance(String param1, String param2) {
        Setting fragment = new Setting();
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
        //个人信息
        inituser();
        //修改登录密码
        initmodifypassword();
        //修改启动密码
        initmodifyczypassword();
        //版本信息
        initversion();
        //联系我们
        initconnectus();
    }

    private void initconnectus() {
        connectus = (LinearLayout)getActivity().findViewById(R.id.connectus);
        connectus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + "15943228711"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initversion() {
        versioninformation = (LinearLayout)getActivity().findViewById(R.id.versioninformation);
        versioninformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Versioninformation.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initmodifyczypassword() {
        modifyczpassword = (LinearLayout)getActivity().findViewById(R.id.modifyczpassword);
        modifyczpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Modifyczpassword.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initmodifypassword() {
        modifypassword = (LinearLayout)getActivity().findViewById(R.id.modifypassword);
        modifypassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Modifzpassword.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void inituser() {
        userInformation = (LinearLayout)getActivity().findViewById(R.id.userInformation);
        userInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UserInformation.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("ID", ID);
                bundle.putString("usertype",usertype);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
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
