package com.kaituocn.govstar.entity;

public class DuBanEntity {
    private String endTime;
    private String mainCompany;
    private String mainName;
    private int supervisionId;
    private String supervisionTitle;
    private String taskState;
    private String transactor;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMainCompany() {
        return mainCompany;
    }

    public void setMainCompany(String mainCompany) {
        this.mainCompany = mainCompany;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public int getSupervisionId() {
        return supervisionId;
    }

    public void setSupervisionId(int supervisionId) {
        this.supervisionId = supervisionId;
    }

    public String getSupervisionTitle() {
        return supervisionTitle;
    }

    public void setSupervisionTitle(String supervisionTitle) {
        this.supervisionTitle = supervisionTitle;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTransactor() {
        return transactor;
    }

    public void setTransactor(String transactor) {
        this.transactor = transactor;
    }
}
