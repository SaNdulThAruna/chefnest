package com.sandul.chefnest.model;

public class UserItem {

    private int id;
    private String userName;
    private String userEmail;
    private int userStatus;
    private String userImage;

    public UserItem(int id, String userName, String userEmail, int userStatus, String userImage) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.userImage = userImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
