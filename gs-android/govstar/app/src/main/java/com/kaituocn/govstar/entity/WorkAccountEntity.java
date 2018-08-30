package com.kaituocn.govstar.entity;

public class WorkAccountEntity {

    private int supervisionId;
    private String supervisionTitle;
    private String createTime;
    private String endActualTime;
    private String compName;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndActualTime() {
        return endActualTime;
    }

    public void setEndActualTime(String endActualTime) {
        this.endActualTime = endActualTime;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }
}
