package amu.roboclub.models;

public class News {
    public String date;
    public String notice;
    public String link;

    public News(){}

    public News(String date, String notice) {
        this.date = date;
        this.notice = notice;
    }

    public News(String date, String notice, String link) {
        this.date = date;
        this.notice = notice;
        this.link = link;
    }

    @Override
    public String toString() {
        return date + " : \n\n" + notice + "\nLink : " + link + "\n\n\n";
    }
}
