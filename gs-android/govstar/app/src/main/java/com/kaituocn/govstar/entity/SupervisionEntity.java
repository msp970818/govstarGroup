package com.kaituocn.govstar.entity;

import java.io.Serializable;
import java.util.List;

public class SupervisionEntity {


    private long endTime;
    private String feedbackType;
    private List<UploadFile> filesUrlArray;
    private String isDelay;
    private String isFeedbak;
    private String isNoticeLeader;
    private String noticeType;
    private String supervisionInfo;
    private String supervisionNumber;
    private String supervisionTitle;
    private String supervisionType;
    private String remarks;
    private Unit userList;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public List<UploadFile> getFilesUrlArray() {
        return filesUrlArray;
    }

    public void setFilesUrlArray(List<UploadFile> filesUrlArray) {
        this.filesUrlArray = filesUrlArray;
    }

    public String getIsDelay() {
        return isDelay;
    }

    public void setIsDelay(String isDelay) {
        this.isDelay = isDelay;
    }

    public String getIsFeedbak() {
        return isFeedbak;
    }

    public void setIsFeedbak(String isFeedbak) {
        this.isFeedbak = isFeedbak;
    }

    public String getIsNoticeLeader() {
        return isNoticeLeader;
    }

    public void setIsNoticeLeader(String isNoticeLeader) {
        this.isNoticeLeader = isNoticeLeader;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getSupervisionInfo() {
        return supervisionInfo;
    }

    public void setSupervisionInfo(String supervisionInfo) {
        this.supervisionInfo = supervisionInfo;
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

    public Unit getUserList() {
        return userList;
    }

    public void setUserList(Unit userList) {
        this.userList = userList;
    }

    public static class UploadFile implements Serializable{

        private String fileName;
        private String fileUrl;

        public UploadFile() {
        }

        public UploadFile(String fileName, String fileUrl) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    public static class Unit{


        private List<Integer> leaderAccs;
        private List<Integer> firstComps;
        private List<Integer> secondComps;

        public Unit() {
        }

        public Unit(List<Integer> leaderAccs, List<Integer> firstComps, List<Integer> secondComps) {
            this.leaderAccs = leaderAccs;
            this.firstComps = firstComps;
            this.secondComps = secondComps;
        }

        public List<Integer> getLeaderAccs() {
            return leaderAccs;
        }

        public void setLeaderAccs(List<Integer> leaderAccs) {
            this.leaderAccs = leaderAccs;
        }

        public List<Integer> getFirstComps() {
            return firstComps;
        }

        public void setFirstComps(List<Integer> firstComps) {
            this.firstComps = firstComps;
        }

        public List<Integer> getSecondComps() {
            return secondComps;
        }

        public void setSecondComps(List<Integer> secondComps) {
            this.secondComps = secondComps;
        }
    }
}
