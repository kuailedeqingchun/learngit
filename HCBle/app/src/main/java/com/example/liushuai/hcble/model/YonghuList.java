package com.example.liushuai.hcble.model;

/**
 * Created by liushuai on 2016/9/22.
 */
public class YonghuList {
    public String pname;//姓名
    public String phoneNumber;//电话号
    public String bumenType;//所属部门
    public boolean isCheck;//是否被选中
    public String yonghuID;//用户ID
    public String guanliID;//管理员ID

    public String getPname() {
        return pname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBumenType() {
        return bumenType;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBumenType(String bumenType) {
        this.bumenType = bumenType;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getGuanliID() {
        return guanliID;
    }

    public void setGuanliID(String guanliID) {
        this.guanliID = guanliID;
    }

    public String getYonghuID() {
        return yonghuID;
    }

    public void setYonghuID(String yonghuID) {
        this.yonghuID = yonghuID;
    }
}
