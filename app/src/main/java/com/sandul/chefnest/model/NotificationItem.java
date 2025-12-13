package com.sandul.chefnest.model;

public class NotificationItem {

    private String notificationTitle;
    private String notificationContent;
    private String notificationDate;

    private String notificationImage;

    public NotificationItem(String notificationTitle, String notificationContent, String notificationDate, String notificationImage) {
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notificationDate = notificationDate;
        this.notificationImage = notificationImage;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }
}
