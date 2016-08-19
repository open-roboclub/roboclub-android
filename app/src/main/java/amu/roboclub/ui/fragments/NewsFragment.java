package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Announcement;
import amu.roboclub.parsers.AnnouncementLoader;
import amu.roboclub.parsers.AnnouncementParser;
import amu.roboclub.ui.adapters.AnnouncementAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements AnnouncementLoader {
    private List<Announcement> news = new ArrayList<>();
    private AnnouncementAdapter announcementAdapter;
    private RecyclerView recyclerView;

    private View root;

    @Override
    public void onAnnouncementsLoaded(List<Announcement> announcements) {
        if (news.size() > 0)
            news.clear();
        news.addAll(announcements);

        announcementAdapter.notifyDataSetChanged();
        hideLoader();
    }

    @Override
    public void onError(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AnnouncementParser announcementParser = new AnnouncementParser(NewsFragment.this);
                        announcementParser.getAnnouncements();
                    }
                }).show();
        showLoader();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AnnouncementParser announcementParser = new AnnouncementParser(this);
        announcementParser.getAnnouncements();

        announcementAdapter = new AnnouncementAdapter(getContext(), news);
        recyclerView.setAdapter(announcementAdapter);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnnouncementParser announcementParser = new AnnouncementParser(NewsFragment.this);
                announcementParser.getAnnouncements();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return root;
    }

    private void hideLoader() {
        LinearLayout loading = (LinearLayout) root.findViewById(R.id.no_news);
        loading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoader() {
        LinearLayout loading = (LinearLayout) root.findViewById(R.id.no_news);
        loading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
