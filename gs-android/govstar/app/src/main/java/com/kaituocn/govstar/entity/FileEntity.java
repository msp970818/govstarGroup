package com.kaituocn.govstar.entity;

public class FileEntity {

    private String addOrUpdate;
    private String comName;
    private String filesTitle;
    private String filesUrl;
    private int id;
    private String uploadName;
    private String uploadTime;

    public String getAddOrUpdate() {
        return addOrUpdate;
    }

    public void setAddOrUpdate(String addOrUpdate) {
        this.addOrUpdate = addOrUpdate;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getFilesTitle() {
        return filesTitle;
    }

    public void setFilesTitle(String filesTitle) {
        this.filesTitle = filesTitle;
    }

    public String getFilesUrl() {
        return filesUrl;
    }

    public void setFilesUrl(String filesUrl) {
        this.filesUrl = filesUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
