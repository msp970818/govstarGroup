package com.kaituocn.govstar.entity;

public class SysNoticeEntity {

    private String appFileUrl;
    private int appOrPc;
    private long createTime;
    private int createUserId;
    private String createUserName;
    private int handState;
    private int id;
    private String pushCompanyId;
    private String pushContent;
    private String pushTitle;
    private String system;

    public String getAppFileUrl() {
        return appFileUrl;
    }

    public void setAppFileUrl(String appFileUrl) {
        this.appFileUrl = appFileUrl;
    }

    public int getAppOrPc() {
        return appOrPc;
    }

    public void setAppOrPc(int appOrPc) {
        this.appOrPc = appOrPc;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public int getHandState() {
        return handState;
    }

    public void setHandState(int handState) {
        this.handState = handState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPushCompanyId() {
        return pushCompanyId;
    }

    public void setPushCompanyId(String pushCompanyId) {
        this.pushCompanyId = pushCompanyId;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
