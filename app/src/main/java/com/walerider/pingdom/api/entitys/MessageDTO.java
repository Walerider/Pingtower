package com.walerider.pingdom.api.entitys;

public class MessageDTO {
    String message;

    @Override
    public String toString() {
        return "MessageDTO{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
