package com.kaituocn.govstar.entity;

import java.io.Serializable;

public class ApprovalEntity implements Serializable{

    private long applyTime;
    private int approverDeptid;
    private int approverId;
    private String approverName;
    private String askedCompleteTime;
    private int checkState;
    private long checkTime;
    private int delFlag;
    private String delayComName;
    private String delayDeptName;
    private String delayText;
    private long delayTime;
    private String delayTitle;
    private String delayUserName;
    private String foundersCompanyName;
    private int id;
    private int pendingState;
    private int resultState;
    private int submitterDeptid;
    private int submitterId;
    private int surplusDay;
    private String tanskNum;
    private int taskId;
    private String checkText;

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public int getApproverDeptid() {
        return approverDeptid;
    }

    public void setApproverDeptid(int approverDeptid) {
        this.approverDeptid = approverDeptid;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getAskedCompleteTime() {
        return askedCompleteTime;
    }

    public void setAskedCompleteTime(String askedCompleteTime) {
        this.askedCompleteTime = askedCompleteTime;
    }

    public int getCheckState() {
        return checkState;
    }

    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelayComName() {
        return delayComName;
    }

    public void setDelayComName(String delayComName) {
        this.delayComName = delayComName;
    }

    public String getDelayDeptName() {
        return delayDeptName;
    }

    public void setDelayDeptName(String delayDeptName) {
        this.delayDeptName = delayDeptName;
    }

    public String getDelayText() {
        return delayText;
    }

    public void setDelayText(String delayText) {
        this.delayText = delayText;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public String getDelayTitle() {
        return delayTitle;
    }

    public void setDelayTitle(String delayTitle) {
        this.delayTitle = delayTitle;
    }

    public String getDelayUserName() {
        return delayUserName;
    }

    public void setDelayUserName(String delayUserName) {
        this.delayUserName = delayUserName;
    }

    public String getFoundersCompanyName() {
        return foundersCompanyName;
    }

    public void setFoundersCompanyName(String foundersCompanyName) {
        this.foundersCompanyName = foundersCompanyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPendingState() {
        return pendingState;
    }

    public void setPendingState(int pendingState) {
        this.pendingState = pendingState;
    }

    public int getResultState() {
        return resultState;
    }

    public void setResultState(int resultState) {
        this.resultState = resultState;
    }

    public int getSubmitterDeptid() {
        return submitterDeptid;
    }

    public void setSubmitterDeptid(int submitterDeptid) {
        this.submitterDeptid = submitterDeptid;
    }

    public int getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(int submitterId) {
        this.submitterId = submitterId;
    }

    public int getSurplusDay() {
        return surplusDay;
    }

    public void setSurplusDay(int surplusDay) {
        this.surplusDay = surplusDay;
    }

    public String getTanskNum() {
        return tanskNum;
    }

    public void setTanskNum(String tanskNum) {
        this.tanskNum = tanskNum;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getCheckText() {
        return checkText;
    }

    public void setCheckText(String checkText) {
        this.checkText = checkText;
    }
}
