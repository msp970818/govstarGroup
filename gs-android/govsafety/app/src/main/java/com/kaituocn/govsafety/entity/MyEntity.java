package com.kaituocn.govsafety.entity;

public class MyEntity {

    private int id;
    private String name;

    private int myType;
    private String myTypeTitle;

    public MyEntity() {
    }

    public MyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMyType() {
        return myType;
    }

    public void setMyType(int myType) {
        this.myType = myType;
    }

    public String getMyTypeTitle() {
        return myTypeTitle;
    }

    public void setMyTypeTitle(String myTypeTitle) {
        this.myTypeTitle = myTypeTitle;
    }
}
