package com.example.liushuai.hcble.model;

/**
 * Created by liushuai on 2016/9/23.
 */
public class UseInfo {
    public String useName;//使用人姓名
    public String useID;//使用人ID
    public String equipmentID;//设备ID
    public String useTime;//使用时间
    public String useAdress;//使用地址
    public String sqReason;//授权原因

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public String getUseID() {
        return useID;
    }

    public void setUseID(String useID) {
        this.useID = useID;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getUseAdress() {
        return useAdress;
    }

    public void setUseAdress(String useAdress) {
        this.useAdress = useAdress;
    }

    public String getSqReason() {
        return sqReason;
    }

    public void setSqReason(String sqReason) {
        this.sqReason = sqReason;
    }
}
