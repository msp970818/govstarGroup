package com.kaituocn.govstar.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class LevelUnitEntity implements Parcelable{

    private int topId;
    private String topName;
    private int id;
    private String name;
    private String avatarUrl;

    private boolean isCheck;

    private boolean showItem;

    public LevelUnitEntity() {
    }

    protected LevelUnitEntity(Parcel in) {
        topId = in.readInt();
        topName = in.readString();
        id = in.readInt();
        name = in.readString();
        avatarUrl = in.readString();
        isCheck = in.readByte() != 0;
        showItem = in.readByte() != 0;
    }

    public static final Creator<LevelUnitEntity> CREATOR = new Creator<LevelUnitEntity>() {
        @Override
        public LevelUnitEntity createFromParcel(Parcel in) {
            return new LevelUnitEntity(in);
        }

        @Override
        public LevelUnitEntity[] newArray(int size) {
            return new LevelUnitEntity[size];
        }
    };

    public boolean isShowItem() {
        return showItem;
    }

    public void setShowItem(boolean showItem) {
        this.showItem = showItem;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getTopId() {
        return topId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }

    public String getTopName() {
        return topName;
    }

    public void setTopName(String topName) {
        this.topName = topName;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(topId);
        dest.writeString(topName);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(avatarUrl);
        dest.writeByte((byte) (isCheck ? 1 : 0));
        dest.writeByte((byte) (showItem ? 1 : 0));
    }
}
