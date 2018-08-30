package com.kaituocn.govstar.entity;

public class PersonScheduleEntity {

    private String name;
    private ScheduleEntity[] body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScheduleEntity[] getBody() {
        return body;
    }

    public void setBody(ScheduleEntity[] body) {
        this.body = body;
    }
}
