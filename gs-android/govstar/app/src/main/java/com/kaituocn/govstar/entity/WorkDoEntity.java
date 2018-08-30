package com.kaituocn.govstar.entity;

import java.io.Serializable;

public class WorkDoEntity implements Serializable{

    private String supervisionTitle;
    private String supervisionNumber;
    private String supervisionInfo;
    private String supervisionType;
    private String supervisionTypeName;
    private String taskState;
    private int taskId;
    private String score;
    private String progress;
    private String noticeType;
    private String isOverTime;
    private String isNoticeLeader;
    private String isFeedbak;
    private String isDelay;
    private String isBackOffice;
    private String feedbackType;
    private String createUserComps;
    private int createUser;
    private int createType;
    private int delFlag;
    private long endTime;
    private int id;
    private UserList userList;

    public String getSupervisionTitle() {
        return supervisionTitle;
    }

    public void setSupervisionTitle(String supervisionTitle) {
        this.supervisionTitle = supervisionTitle;
    }

    public String getSupervisionNumber() {
        return supervisionNumber;
    }

    public void setSupervisionNumber(String supervisionNumber) {
        this.supervisionNumber = supervisionNumber;
    }

    public String getSupervisionInfo() {
        return supervisionInfo;
    }

    public void setSupervisionInfo(String supervisionInfo) {
        this.supervisionInfo = supervisionInfo;
    }

    public String getSupervisionType() {
        return supervisionType;
    }

    public void setSupervisionType(String supervisionType) {
        this.supervisionType = supervisionType;
    }

    public String getSupervisionTypeName() {
        return supervisionTypeName;
    }

    public void setSupervisionTypeName(String supervisionTypeName) {
        this.supervisionTypeName = supervisionTypeName;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getIsOverTime() {
        return isOverTime;
    }

    public void setIsOverTime(String isOverTime) {
        this.isOverTime = isOverTime;
    }

    public String getIsNoticeLeader() {
        return isNoticeLeader;
    }

    public void setIsNoticeLeader(String isNoticeLeader) {
        this.isNoticeLeader = isNoticeLeader;
    }

    public String getIsFeedbak() {
        return isFeedbak;
    }

    public void setIsFeedbak(String isFeedbak) {
        this.isFeedbak = isFeedbak;
    }

    public String getIsDelay() {
        return isDelay;
    }

    public void setIsDelay(String isDelay) {
        this.isDelay = isDelay;
    }

    public String getIsBackOffice() {
        return isBackOffice;
    }

    public void setIsBackOffice(String isBackOffice) {
        this.isBackOffice = isBackOffice;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getCreateUserComps() {
        return createUserComps;
    }

    public void setCreateUserComps(String createUserComps) {
        this.createUserComps = createUserComps;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public int getCreateType() {
        return createType;
    }

    public void setCreateType(int createType) {
        this.createType = createType;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserList getUserList() {
        return userList;
    }

    public void setUserList(UserList userList) {
        this.userList = userList;
    }

    public static class UserList implements Serializable{
        private Company[] leaderAccs;
        private Company[] firstComps;
        private Company[] secondComps;

        public Company[] getLeaderAccs() {
            return leaderAccs;
        }

        public void setLeaderAccs(Company[] leaderAccs) {
            this.leaderAccs = leaderAccs;
        }

        public Company[] getFirstComps() {
            return firstComps;
        }

        public void setFirstComps(Company[] firstComps) {
            this.firstComps = firstComps;
        }

        public Company[] getSecondComps() {
            return secondComps;
        }

        public void setSecondComps(Company[] secondComps) {
            this.secondComps = secondComps;
        }
    }

    public static class Company implements Serializable{
        private String comName;
        private String userName;

        public String getComName() {
            return comName;
        }

        public void setComName(String comName) {
            this.comName = comName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
