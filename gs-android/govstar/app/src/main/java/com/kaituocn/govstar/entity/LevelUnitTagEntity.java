package com.kaituocn.govstar.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LevelUnitTagEntity implements Parcelable{


    private int id;
    private String name;
    private List<ChildItem> children;

    public LevelUnitTagEntity() {
    }

    protected LevelUnitTagEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        children = in.createTypedArrayList(ChildItem.CREATOR);
    }

    public static final Creator<LevelUnitTagEntity> CREATOR = new Creator<LevelUnitTagEntity>() {
        @Override
        public LevelUnitTagEntity createFromParcel(Parcel in) {
            return new LevelUnitTagEntity(in);
        }

        @Override
        public LevelUnitTagEntity[] newArray(int size) {
            return new LevelUnitTagEntity[size];
        }
    };

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

    public List<ChildItem> getChildren() {
        return children;
    }

    public void setChildren(List<ChildItem> children) {
        this.children = children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(children);
    }

    public static class ChildItem implements Parcelable{
        private int id;
        private String name;
        private String avatarUrl;
        private boolean isCheck;

        public ChildItem() {
        }

        protected ChildItem(Parcel in) {
            id = in.readInt();
            name = in.readString();
            avatarUrl = in.readString();
            isCheck = in.readByte() != 0;
        }

        public static final Creator<ChildItem> CREATOR = new Creator<ChildItem>() {
            @Override
            public ChildItem createFromParcel(Parcel in) {
                return new ChildItem(in);
            }

            @Override
            public ChildItem[] newArray(int size) {
                return new ChildItem[size];
            }
        };

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

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
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

}
