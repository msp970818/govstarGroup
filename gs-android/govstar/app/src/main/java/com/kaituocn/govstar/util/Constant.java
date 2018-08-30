package com.kaituocn.govstar.util;

import com.kaituocn.govstar.entity.UserEntity;

public class Constant {

    public static boolean DEBUG = false;

    public static int NOTICE_NUM;

    public static UserEntity userEntity=new UserEntity();

    public static String FILE_BASE_URL ="";

    public static String getMeetingName(int type) {
        String name ;
        switch (type) {
            case 1:
                name="现场会议";
                break;
            case 2:
                name="音视频通话";
                break;
            case 3:
                name="直播会议";
                break;
            default:
                name = "会议";
                break;
        }
        return name;
    }

}
