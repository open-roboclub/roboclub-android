package amu.roboclub.parsers;

import amu.roboclub.models.Announcement;

import java.util.List;

public interface AnnouncementLoader {
    void onAnnouncementsLoaded(List<Announcement> announcements);
}
