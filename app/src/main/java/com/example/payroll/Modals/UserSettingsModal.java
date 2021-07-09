package com.example.payroll.Modals;

public class UserSettingsModal {

    private int usID;
    private String usLogged;
    private String usUserCode;
    private String usRegDate;

    public UserSettingsModal(int usID, String usLogged, String usUserCode, String usRegDate) {
        this.usID = usID;
        this.usLogged = usLogged;
        this.usUserCode = usUserCode;
        this.usRegDate = usRegDate;
    }

    public UserSettingsModal(String usLogged, String usUserCode) {
        this.usLogged = usLogged;
        this.usUserCode = usUserCode;
    }

    public int getUsID() {
        return usID;
    }

    public String getUsLogged() {
        return usLogged;
    }

    public String getUsUserCode() {
        return usUserCode;
    }

    public String getUsRegDate() {
        return usRegDate;
    }
}
