package com.kaituocn.govstar.entity;

public class NoticePersonEntity {

    private String personComName;
    private int personId;
    private String personName;
    private int receiptState;
    private String receiptTime;
    private int tabId;
    private int singState;
    private String isLeave;

    private int myType;

    public int getSingState() {
        return singState;
    }

    public void setSingState(int singState) {
        this.singState = singState;
    }

    public String getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(String isLeave) {
        this.isLeave = isLeave;
    }

    public String getPersonComName() {
        return personComName;
    }

    public void setPersonComName(String personComName) {
        this.personComName = personComName;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int getReceiptState() {
        return receiptState;
    }

    public void setReceiptState(int receiptState) {
        this.receiptState = receiptState;
    }

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public int getMyType() {
        return myType;
    }

    public void setMyType(int myType) {
        this.myType = myType;
    }
}
