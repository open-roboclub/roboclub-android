package amu.roboclub.models;

public class Announcement {
    public String date;
    public String message;
    public String attachment;

    public Announcement(String date) {
        this.date = date;
    }

    public Announcement(String date, String message) {
        this.date = date;
        this.message = message;
    }

    public Announcement(String date, String message, String attachment) {
        this.date = date;
        this.message = message;
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return date + " : \n\n" + message + "\nAttachment : " + attachment + "\n\n\n";
    }
}
