package com.example.liushuai.hcble.model;

/**
 * Created by liushuai on 2016/9/23.
 */
public class EquipmentInfo {
    public String equipmentType;//设备类型
    public String equipmentID;//设备ID
    public String equipmentCode;//设备编码
    public String equipmentName;//设备名称
    public String ssCustomer;//所属客户
    public String useState;//使用状态

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getSsCustomer() {
        return ssCustomer;
    }

    public void setSsCustomer(String ssCustomer) {
        this.ssCustomer = ssCustomer;
    }

    public String getUseState() {
        return useState;
    }

    public void setUseState(String useState) {
        this.useState = useState;
    }
}
