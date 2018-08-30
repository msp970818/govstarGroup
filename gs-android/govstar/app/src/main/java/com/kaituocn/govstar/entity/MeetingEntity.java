package com.kaituocn.govstar.entity;

import java.io.Serializable;
import java.util.List;

public class MeetingEntity implements Serializable{
    private String id;
    private String meetingTitile;
    private String mettingNum;
    private int mettingType;
    private String mettingRoomId;
    private String mettingRoomName;
    private String createUserId;
    private String joinUserId;
    private int mettingState;
    private String remarksInfo;
    private String createTime;
    private long endTime;
    private long mettringStartTime;
    private long mtstartTime;
    private String piid;
    private String longitudeLatitude;
    private String mettingDescribe;
    private String createName;
    private String singTitle;
    private String joinName;
    private int showState;
    private int singState;

    private String isOverdue;

    private String attendPersonIds;
    private String attendName;
    private String attendavatarUrl;

    public String getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(String isOverdue) {
        this.isOverdue = isOverdue;
    }

    public String getAttendPersonIds() {
        return attendPersonIds;
    }

    public void setAttendPersonIds(String attendPersonIds) {
        this.attendPersonIds = attendPersonIds;
    }

    public String getAttendName() {
        return attendName;
    }

    public void setAttendName(String attendName) {
        this.attendName = attendName;
    }

    public String getAttendavatarUrl() {
        return attendavatarUrl;
    }

    public void setAttendavatarUrl(String attendavatarUrl) {
        this.attendavatarUrl = attendavatarUrl;
    }

    private List<SupervisionEntity.UploadFile> filesUrlArray;
    private Room room;

    public List<SupervisionEntity.UploadFile> getFilesUrlArray() {
        return filesUrlArray;
    }

    public void setFilesUrlArray(List<SupervisionEntity.UploadFile> filesUrlArray) {
        this.filesUrlArray = filesUrlArray;
    }

    public int getSingState() {
        return singState;
    }

    public void setSingState(int singState) {
        this.singState = singState;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingTitile() {
        return meetingTitile;
    }

    public void setMeetingTitile(String meetingTitile) {
        this.meetingTitile = meetingTitile;
    }

    public String getMettingNum() {
        return mettingNum;
    }

    public void setMettingNum(String mettingNum) {
        this.mettingNum = mettingNum;
    }

    public int getMettingType() {
        return mettingType;
    }

    public void setMettingType(int mettingType) {
        this.mettingType = mettingType;
    }

    public String getMettingRoomId() {
        return mettingRoomId;
    }

    public void setMettingRoomId(String mettingRoomId) {
        this.mettingRoomId = mettingRoomId;
    }

    public String getMettingRoomName() {
        return mettingRoomName;
    }

    public void setMettingRoomName(String mettingRoomName) {
        this.mettingRoomName = mettingRoomName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getJoinUserId() {
        return joinUserId;
    }

    public void setJoinUserId(String joinUserId) {
        this.joinUserId = joinUserId;
    }



    public String getRemarksInfo() {
        return remarksInfo;
    }

    public void setRemarksInfo(String remarksInfo) {
        this.remarksInfo = remarksInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getMettingState() {
        return mettingState;
    }

    public void setMettingState(int mettingState) {
        this.mettingState = mettingState;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getMettringStartTime() {
        return mettringStartTime;
    }

    public void setMettringStartTime(long mettringStartTime) {
        this.mettringStartTime = mettringStartTime;
    }

    public long getMtstartTime() {
        return mtstartTime;
    }

    public void setMtstartTime(long mtstartTime) {
        this.mtstartTime = mtstartTime;
    }

    public String getPiid() {
        return piid;
    }

    public void setPiid(String piid) {
        this.piid = piid;
    }

    public String getLongitudeLatitude() {
        return longitudeLatitude;
    }

    public void setLongitudeLatitude(String longitudeLatitude) {
        this.longitudeLatitude = longitudeLatitude;
    }

    public String getMettingDescribe() {
        return mettingDescribe;
    }

    public void setMettingDescribe(String mettingDescribe) {
        this.mettingDescribe = mettingDescribe;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getSingTitle() {
        return singTitle;
    }

    public void setSingTitle(String singTitle) {
        this.singTitle = singTitle;
    }

    public int getShowState() {
        return showState;
    }

    public void setShowState(int showState) {
        this.showState = showState;
    }

    @Override
    public String toString() {
        return "MeetingEntity{" +
                "id='" + id + '\'' +
                ", meetingTitile='" + meetingTitile + '\'' +
                ", mettingNum='" + mettingNum + '\'' +
                ", mettingType='" + mettingType + '\'' +
                ", mettingRoomId='" + mettingRoomId + '\'' +
                ", mettingRoomName='" + mettingRoomName + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", joinUserId='" + joinUserId + '\'' +
                ", mettingState='" + mettingState + '\'' +
                ", remarksInfo='" + remarksInfo + '\'' +
                ", createTime='" + createTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", mettringStartTime='" + mettringStartTime + '\'' +
                ", mtstartTime='" + mtstartTime + '\'' +
                ", piid='" + piid + '\'' +
                ", longitudeLatitude='" + longitudeLatitude + '\'' +
                ", mettingDescribe='" + mettingDescribe + '\'' +
                ", createName='" + createName + '\'' +
                ", singTitle='" + singTitle + '\'' +
                ", showState='" + showState + '\'' +
                ", room=" + room.toString() +
                '}';
    }

    public static class Room implements Serializable{

        //-----------直播前
        private String httpPullUrl;
        private String hlsPullUrl;
        private String rtmpPullUrl;
        private String name;
        private String pushUrl;
        private String ctime;
        private String cid;

        //------------直播完
        private String createTime;
        private String origUrl;
        private String downloadOrigUrl;
        private String updateTime;
        private String status;
        private String width;
        private String videoName;
        private String typeName;
        private String duration;
        private String height;
        private String snapshotUrl;
        private String initialSize;
        private String vid;
        private String typeId;
        private String durationMsec;

        public String getHttpPullUrl() {
            return httpPullUrl;
        }

        public void setHttpPullUrl(String httpPullUrl) {
            this.httpPullUrl = httpPullUrl;
        }

        public String getHlsPullUrl() {
            return hlsPullUrl;
        }

        public void setHlsPullUrl(String hlsPullUrl) {
            this.hlsPullUrl = hlsPullUrl;
        }

        public String getRtmpPullUrl() {
            return rtmpPullUrl;
        }

        public void setRtmpPullUrl(String rtmpPullUrl) {
            this.rtmpPullUrl = rtmpPullUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getOrigUrl() {
            return origUrl;
        }

        public void setOrigUrl(String origUrl) {
            this.origUrl = origUrl;
        }

        public String getDownloadOrigUrl() {
            return downloadOrigUrl;
        }

        public void setDownloadOrigUrl(String downloadOrigUrl) {
            this.downloadOrigUrl = downloadOrigUrl;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getSnapshotUrl() {
            return snapshotUrl;
        }

        public void setSnapshotUrl(String snapshotUrl) {
            this.snapshotUrl = snapshotUrl;
        }

        public String getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(String initialSize) {
            this.initialSize = initialSize;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getDurationMsec() {
            return durationMsec;
        }

        public void setDurationMsec(String durationMsec) {
            this.durationMsec = durationMsec;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "httpPullUrl='" + httpPullUrl + '\'' +
                    ", hlsPullUrl='" + hlsPullUrl + '\'' +
                    ", rtmpPullUrl='" + rtmpPullUrl + '\'' +
                    ", name='" + name + '\'' +
                    ", pushUrl='" + pushUrl + '\'' +
                    ", ctime='" + ctime + '\'' +
                    ", cid='" + cid + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", origUrl='" + origUrl + '\'' +
                    ", downloadOrigUrl='" + downloadOrigUrl + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", status='" + status + '\'' +
                    ", width='" + width + '\'' +
                    ", videoName='" + videoName + '\'' +
                    ", typeName='" + typeName + '\'' +
                    ", duration='" + duration + '\'' +
                    ", height='" + height + '\'' +
                    ", snapshotUrl='" + snapshotUrl + '\'' +
                    ", initialSize='" + initialSize + '\'' +
                    ", vid='" + vid + '\'' +
                    ", typeId='" + typeId + '\'' +
                    ", durationMsec='" + durationMsec + '\'' +
                    '}';
        }
    }
}
