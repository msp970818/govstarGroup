package com.kaituocn.govstar.event;

public class RefreshEvent {

   public static int Refresh_WorkList;
   public static int Refresh_MySchedule;

    private int type;

    public RefreshEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
