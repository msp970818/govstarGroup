package com.kaituocn.govstar.entity;

public class OptionEntity {
    private int key;
    private String value;

    public OptionEntity() {
    }

    public OptionEntity(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
