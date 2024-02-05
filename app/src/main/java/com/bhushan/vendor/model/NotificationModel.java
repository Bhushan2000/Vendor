package com.bhushan.vendor.model;

public class NotificationModel {
    private int id;
    private String notificationTitle;
    private String notificationImage;
    private String notificationMessage;
    private String NotificationDateTime;
    private int readed;


    public NotificationModel(int id, String notificationTitle, String notificationImage, String notificationMessage, String notificationDateTime, int readed) {
        this.id = id;
        this.notificationTitle = notificationTitle;
        this.notificationImage = notificationImage;
        this.notificationMessage = notificationMessage;
        NotificationDateTime = notificationDateTime;
        this.readed = readed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }


    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationDateTime() {
        return NotificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        NotificationDateTime = notificationDateTime;
    }
}