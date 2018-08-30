package com.kaituocn.govsafety.entity;

public class SetEntity {

    private int openTemporary;
    private int verification;

    private String firstDate;
    private String name;
    private String imei;
    private String userName;
    private String brand;

    public int getOpenTemporary() {
        return openTemporary;
    }

    public void setOpenTemporary(int openTemporary) {
        this.openTemporary = openTemporary;
    }

    public int getVerification() {
        return verification;
    }

    public void setVerification(int verification) {
        this.verification = verification;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
