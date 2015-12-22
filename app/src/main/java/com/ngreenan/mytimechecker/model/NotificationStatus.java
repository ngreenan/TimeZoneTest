package com.ngreenan.mytimechecker.model;

/**
 * Created by Nick on 03/12/2015.
 */
public class NotificationStatus {
    private long personID;
    private int notificationStatus;

    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public int getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(int notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    @Override
    public String toString() {
        return notificationStatus == 1 ? "true" : "false";
    }
}
