package com.kaituocn.govstar.entity;

import java.io.Serializable;

public class ScheduleEntity implements Serializable{

    private int id;
    private String scheduleTitle;
    private int scheduleType;
    private String scheduleDescribe;
    private int scheduleImportance;
    private String accountIds;
    private String startTime;
    private String endTime;
    private String scheduleNumber;
    private int completeType;

    public int getCompleteType() {
        return completeType;
    }

    public void setCompleteType(int completeType) {
        this.completeType = completeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleDescribe() {
        return scheduleDescribe;
    }

    public void setScheduleDescribe(String scheduleDescribe) {
        this.scheduleDescribe = scheduleDescribe;
    }

    public int getScheduleImportance() {
        return scheduleImportance;
    }

    public void setScheduleImportance(int scheduleImportance) {
        this.scheduleImportance = scheduleImportance;
    }

    public String getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(String accountIds) {
        this.accountIds = accountIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }
}
