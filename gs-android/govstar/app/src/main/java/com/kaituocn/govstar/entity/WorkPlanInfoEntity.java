package com.kaituocn.govstar.entity;

import java.io.Serializable;

public class WorkPlanInfoEntity implements Serializable{

    private String depId;
    private String priority;
    private String planInfo;
    private long endTime;
    private String userId;

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPlanInfo() {
        return planInfo;
    }

    public void setPlanInfo(String planInfo) {
        this.planInfo = planInfo;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "WorkPlanInfoEntity{" +
                "depId='" + depId + '\'' +
                ", priority='" + priority + '\'' +
                ", planInfo='" + planInfo + '\'' +
                ", endTime=" + endTime +
                ", userId='" + userId + '\'' +
                '}';
    }
}
