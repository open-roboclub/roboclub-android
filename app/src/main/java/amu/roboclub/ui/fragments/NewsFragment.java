package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Announcement;
import amu.roboclub.parsers.AnnouncementLoader;
import amu.roboclub.parsers.AnnouncementParser;
import amu.roboclub.ui.adapters.AnnouncementAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements AnnouncementLoader {
    private List<Announcement> news = new ArrayList<>();
    private AnnouncementAdapter announcementAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onAnnouncementsLoaded(List<Announcement> announcements) {
        if (news.size() > 0)
            news.clear();
        news.addAll(announcements);

        if (announcementAdapter == null) {
            announcementAdapter = new AnnouncementAdapter(getContext(), news);
        } else {
            announcementAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AnnouncementParser announcementParser = new AnnouncementParser(this);
        announcementParser.getAnnouncements();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (announcementAdapter == null)
            announcementAdapter = new AnnouncementAdapter(getContext(), news);
        recyclerView.setAdapter(announcementAdapter);

        return root;
    }
}
