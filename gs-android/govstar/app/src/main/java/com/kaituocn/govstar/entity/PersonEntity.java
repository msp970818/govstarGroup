package com.kaituocn.govstar.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonEntity implements Parcelable{

    private int id;
    private String name;
    private String avatarUrl;
    private boolean isCheck;


    protected PersonEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        avatarUrl = in.readString();
        isCheck = in.readByte() != 0;
    }

    public static final Creator<PersonEntity> CREATOR = new Creator<PersonEntity>() {
        @Override
        public PersonEntity createFromParcel(Parcel in) {
            return new PersonEntity(in);
        }

        @Override
        public PersonEntity[] newArray(int size) {
            return new PersonEntity[size];
        }
    };

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public PersonEntity() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(avatarUrl);
        dest.writeByte((byte) (isCheck ? 1 : 0));
    }
}
