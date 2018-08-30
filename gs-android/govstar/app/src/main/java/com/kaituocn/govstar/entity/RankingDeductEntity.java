package com.kaituocn.govstar.entity;

public class RankingDeductEntity {

    private long createTime;
    private String reason;
    private String companyName;
    private int deductTheScore;
    private int deductType;
    private int deductUserId;
    private String deptName;
    private int id;
    private String taskNum;
    private String taskTitle;
    private String totalScore;
    private String userName;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getDeductTheScore() {
        return deductTheScore;
    }

    public void setDeductTheScore(int deductTheScore) {
        this.deductTheScore = deductTheScore;
    }

    public int getDeductType() {
        return deductType;
    }

    public void setDeductType(int deductType) {
        this.deductType = deductType;
    }

    public int getDeductUserId() {
        return deductUserId;
    }

    public void setDeductUserId(int deductUserId) {
        this.deductUserId = deductUserId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
