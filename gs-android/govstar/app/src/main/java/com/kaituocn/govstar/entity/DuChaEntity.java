package com.kaituocn.govstar.entity;

public class DuChaEntity {

    private String createComp;
    private String createDepa;
    private String createTime;
    private int createUser;
    private String endTime;
    private int id;
    private String progress;
    private String supervisionNumber;
    private String supervisionTitle;
    private String supervisionType;
    private int taskId;
    private int taskState;
    private String timeout;
    private int isCheck;
    private int isOverTime;
    private int userCollectionId;

    public int getUserCollectionId() {
        return userCollectionId;
    }

    public void setUserCollectionId(int userCollectionId) {
        this.userCollectionId = userCollectionId;
    }

    public int getIsOverTime() {
        return isOverTime;
    }

    public void setIsOverTime(int isOverTime) {
        this.isOverTime = isOverTime;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public String getCreateComp() {
        return createComp;
    }

    public void setCreateComp(String createComp) {
        this.createComp = createComp;
    }

    public String getCreateDepa() {
        return createDepa;
    }

    public void setCreateDepa(String createDepa) {
        this.createDepa = createDepa;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getSupervisionNumber() {
        return supervisionNumber;
    }

    public void setSupervisionNumber(String supervisionNumber) {
        this.supervisionNumber = supervisionNumber;
    }

    public String getSupervisionTitle() {
        return supervisionTitle;
    }

    public void setSupervisionTitle(String supervisionTitle) {
        this.supervisionTitle = supervisionTitle;
    }

    public String getSupervisionType() {
        return supervisionType;
    }

    public void setSupervisionType(String supervisionType) {
        this.supervisionType = supervisionType;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskState() {
        return taskState;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
