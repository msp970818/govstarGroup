package com.kaituocn.govstar.entity;

public class TodoEntity {

    private String createComp;
    private String createDepa;
    private String createTime;
    private int createUser;
    private String department;
    private String endTime;
    private String firstLeader;
    private int id;
    private String leader;
    private String progress;
    private String supervisionNumber;
    private String supervisionTitle;
    private String supervisionType;
    private String supervisionTypeName;
    private int taskId;
    private String taskState;
    private String timeout;

    public String getSupervisionTypeName() {
        return supervisionTypeName;
    }

    public void setSupervisionTypeName(String supervisionTypeName) {
        this.supervisionTypeName = supervisionTypeName;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFirstLeader() {
        return firstLeader;
    }

    public void setFirstLeader(String firstLeader) {
        this.firstLeader = firstLeader;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
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

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
