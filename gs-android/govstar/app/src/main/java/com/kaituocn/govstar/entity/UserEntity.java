package com.kaituocn.govstar.entity;

import java.util.List;
import java.util.Map;

public class UserEntity {

    private List<AccountInfo> MultiAccountList;
    private PersonInfo personInfo;

    private Map<String,Boolean> powerMap;

    public List<AccountInfo> getMultiAccountList() {
        return MultiAccountList;
    }

    public void setMultiAccountList(List<AccountInfo> multiAccountList) {
        MultiAccountList = multiAccountList;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public Map<String, Boolean> getPowerMap() {
        return powerMap;
    }

    public void setPowerMap(Map<String, Boolean> powerMap) {
        this.powerMap = powerMap;
    }

    public static class AccountInfo{
        private String comName;
        private String depName;
        private int id;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public  static class PersonInfo{
        private String allowVpns;
        private int appId;
        private String authKey;
        private String avatarUrl;
        private String comName;
        private String depName;
        private String forwardPerson;
        private int id;
        private int isSafe;
        private int level;
        private String mobile;
        private String name;
        private String nickName;
        private String role;
        private String sex;
        private String age;
        private int workType;

        public int getWorkType() {
            return workType;
        }

        public void setWorkType(int workType) {
            this.workType = workType;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getAllowVpns() {
            return allowVpns;
        }

        public void setAllowVpns(String allowVpns) {
            this.allowVpns = allowVpns;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
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

        public String getForwardPerson() {
            return forwardPerson;
        }

        public void setForwardPerson(String forwardPerson) {
            this.forwardPerson = forwardPerson;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIsSafe() {
            return isSafe;
        }

        public void setIsSafe(int isSafe) {
            this.isSafe = isSafe;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
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
}
