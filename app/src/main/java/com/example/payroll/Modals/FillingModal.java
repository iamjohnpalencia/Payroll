package com.example.payroll.Modals;

public class FillingModal {

    private int flID;
    private String flUserCode;
    private String flType;
    private String flDateFrom;
    private String flDateTo;
    private String flReason;
    private String flRemarks;
    private String flSync;
    private String flStatus;
    private String flRegDate;

    public FillingModal(String flUserCode, String flType, String flDateFrom, String flDateTo, String flReason, String flRemarks, String flSync, String flStatus) {
        this.flUserCode = flUserCode;
        this.flType = flType;
        this.flDateFrom = flDateFrom;
        this.flDateTo = flDateTo;
        this.flReason = flReason;
        this.flRemarks = flRemarks;
        this.flSync = flSync;
        this.flStatus = flStatus;
    }

    public FillingModal( String flType, String flDateFrom, String flDateTo, String flReason, String flRemarks,String flUserCode, String flStatus){
        this.flType = flType;
        this.flDateFrom = flDateFrom;
        this.flDateTo = flDateTo;
        this.flReason = flReason;
        this.flRemarks = flRemarks;
        this.flUserCode = flUserCode;
        this.flStatus = flStatus;
    }


    public int getFlID() {
        return flID;
    }

    public void setFlID(int flID) {
        this.flID = flID;
    }

    public String getFlUserCode() {
        return flUserCode;
    }

    public void setFlUserCode(String flUserCode) {
        this.flUserCode = flUserCode;
    }

    public String getFlType() {
        return flType;
    }

    public void setFlType(String flType) {
        this.flType = flType;
    }

    public String getFlDateFrom() {
        return flDateFrom;
    }

    public void setFlDateFrom(String flDateFrom) {
        this.flDateFrom = flDateFrom;
    }

    public String getFlDateTo() {
        return flDateTo;
    }

    public void setFlDateTo(String flDateTo) {
        this.flDateTo = flDateTo;
    }

    public String getFlReason() {
        return flReason;
    }

    public void setFlReason(String flReason) {
        this.flReason = flReason;
    }

    public String getFlRemarks() {
        return flRemarks;
    }

    public void setFlRemarks(String flRemarks) {
        this.flRemarks = flRemarks;
    }

    public String getFlSync() {
        return flSync;
    }

    public void setFlSync(String flSync) {
        this.flSync = flSync;
    }

    public String getFlStatus() {
        return flStatus;
    }

    public void setFlStatus(String flStatus) {
        this.flStatus = flStatus;
    }

    public String getFlRegDate() {
        return flRegDate;
    }

    public void setFlRegDate(String flRegDate) {
        this.flRegDate = flRegDate;
    }
}
