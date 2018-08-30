package com.kaituocn.govsafety.entity;

public class UserEntity {

    private String authKey;
    private String comName;
    private String depName;
    private String id;
    private String mobile;
    private String name;
    private String nickName;
    private String role;
    private String lastTimeLoginIp;

    public String getLastTimeLoginIp() {
        return lastTimeLoginIp;
    }

    public void setLastTimeLoginIp(String lastTimeLoginIp) {
        this.lastTimeLoginIp = lastTimeLoginIp;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
