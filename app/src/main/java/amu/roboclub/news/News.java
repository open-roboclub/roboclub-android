package amu.roboclub.news;

import android.support.annotation.Keep;

@Keep
public class News {
    public String title;
    public String date;
    public String notice;
    public String notification;
    public long timestamp;
    public String link;

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", notice='" + notice + '\'' +
                ", notification='" + notification + '\'' +
                ", timestamp=" + timestamp +
                ", link='" + link + '\'' +
                '}';
    }
}
