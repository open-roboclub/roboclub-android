package amu.roboclub.parsers;

import amu.roboclub.models.Announcement;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementParser {
    private List<Announcement> announcements = new ArrayList<>();
    private AnnouncementLoader announcementLoader;
    private String message = null;

    public AnnouncementParser(AnnouncementLoader announcementLoader) {
        this.announcementLoader = announcementLoader;
    }

    public void getAnnouncements() {
        new AnnouncementHandler().execute();
    }

    private Document loadDocument() {
        try {
            return Jsoup.connect("http://www.amuroboclub.in/announcements.php").get();
        } catch (IOException e) {
            return null;
        }
    }

    private void parseDoc() {
        Document page = loadDocument();
        if (page == null) {
            message = "Couldn't Load News. Check connection";
            return;
        }

        Elements dates = page.select("div.date");
        for (Element date : dates) {
            announcements.add(new Announcement(date.text()));
        }

        Elements messages = page.select("div.notice");
        for (int i = 0; i < announcements.size(); i++) {
            Element message = messages.get(i);
            Announcement announcement = announcements.get(i);
            announcement.message = message.text();

            String link = null;
            try {

                if (message.select("a[href]").first() != null)
                    link = message.select("a[href]").first().attr("abs:href");

                try {
                    link = message.select("div.attachment").select("a[href]").first().attr("abs:href");
                } catch (NullPointerException npe) { }

                URL url = new URL(link);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();
                announcement.attachment = url.toString();
            } catch (MalformedURLException mue) {
            } catch (URISyntaxException use) {
            }
        }
    }

    private class AnnouncementHandler extends AsyncTask<Void, Void, List<Announcement>> {

        @Override
        protected List<Announcement> doInBackground(Void... voids) {
            parseDoc();
            return announcements;
        }

        @Override
        protected void onPostExecute(List<Announcement> announcements) {
            super.onPostExecute(announcements);
            if(message==null)
                announcementLoader.onAnnouncementsLoaded(announcements);
            else
                announcementLoader.onError(message);
        }
    }

}
