package com.kaituocn.govstar.entity;

public class WorkPlanTagEntity {

    private int supervisionId;
    private String workType;
    private int id;
    private String comName;
    private int taskId;

    private String isRectification;
    private String comId;

    private int myType;

    public String getIsRectification() {
        return isRectification;
    }

    public void setIsRectification(String isRectification) {
        this.isRectification = isRectification;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public int getMyType() {
        return myType;
    }

    public void setMyType(int myType) {
        this.myType = myType;
    }

    public int getSupervisionId() {
        return supervisionId;
    }

    public void setSupervisionId(int supervisionId) {
        this.supervisionId = supervisionId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
