package com.kaituocn.govstar.event;

public class ChangeHeadEvent {
    private String message;

    public ChangeHeadEvent() {
    }

    public ChangeHeadEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
