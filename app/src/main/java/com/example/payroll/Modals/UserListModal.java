package com.example.payroll.Modals;

import androidx.annotation.Nullable;

public class UserListModal {

    private int id;
    private String companyName;
    private String deviceName;
    private String userCode;
    private String userPass;
    private String timezone;
    private String fullAddress;
    private String logType;
    private String logDesc;
    private String lat;
    private String lnt;
    private String image;
    private String regDate;
    private String verStatus;





    public UserListModal(int id, String companyName, String deviceName, String userCode, String fullAddress, String logType, String logDesc, String lat, String lnt, @Nullable String image) {
        this.id = id;
        this.companyName = companyName;
        this.deviceName = deviceName;
        this.userCode = userCode;
        this.fullAddress = fullAddress;
        this.logType = logType;
        this.logDesc = logDesc;
        this.lat = lat;
        this.lnt = lnt;
        this.image = image;
    }

    public UserListModal(String userCode, String logType, String logDesc, String regDate) {
        this.userCode = userCode;
        this.logType = logType;
        this.logDesc = logDesc;
        this.regDate = regDate;
    }


    public UserListModal(int id, String companyName, String deviceName, String userCode, String userPass, String timezone, String verificationStatus) {
        this.id = id;
        this.companyName = companyName;
        this.deviceName = deviceName;
        this.userCode = userCode;
        this.userPass = userPass;
        this.timezone = timezone;
        this.verStatus = verificationStatus;
    }
    public UserListModal(String userCode, String userPass) {
        this.userCode = userCode;
        this.userPass = userPass;
    }

    public UserListModal(String userCode) {

        this.userCode = userCode;

    }

    public void UserListModalImage(String userImage) {
        this.image = userImage;
    }



    @Override
    public String toString() {
        return "UserListModal{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userPass='" + userPass + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }



    public String getCompanyName() {
        return companyName;
    }


    public String getDeviceName() {
        return deviceName;
    }


    public String getUserCode() {
        return userCode;
    }



    public String getUserPass() {
        return userPass;
    }


    public String getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getLogType() {
        return logType;
    }

    public String getLogDesc() {
        return logDesc;
    }

    public String getLat() {
        return lat;
    }

    public String getLnt() {
        return lnt;
    }

    public String getImage() {
        return image;
    }

    public String getVerStatus() { return verStatus; }
}
