package com.example.liushuai.hcble.engine;

import com.example.liushuai.hcble.model.YonghuList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushuai on 2016/9/22.
 */
public class YonghuProvider {

    public static List<YonghuList> getyonghuList(){
        YonghuList mmyonghu = new YonghuList();
        mmyonghu.setPname("张三");
        mmyonghu.setPhoneNumber("123456789");
        mmyonghu.setBumenType("研发部");
        mmyonghu.setGuanliID("1234");
        List<YonghuList> oyonghuList = new ArrayList<YonghuList>();
        oyonghuList.add(mmyonghu);
        return oyonghuList;
    }
}
