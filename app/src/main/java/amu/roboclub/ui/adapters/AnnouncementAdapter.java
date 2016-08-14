package amu.roboclub.ui.adapters;

import amu.roboclub.R;
import amu.roboclub.models.Announcement;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context context;
    private List<Announcement> announcements;

    public AnnouncementAdapter(Context context, List<Announcement> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Announcement announcement = announcements.get(position);
        holder.news.setText(announcement.message);
        holder.date.setText(announcement.date);

        if (announcement.attachment == null){
            holder.link.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            return;
        }

        holder.link.setVisibility(View.VISIBLE);
        holder.divider.setVisibility(View.VISIBLE);

        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(announcement.attachment));
                try {
                    context.startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(holder.link, "No app can handle the request", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView news, date;
        public LinearLayout link, divider;

        public ViewHolder(View view) {
            super(view);
            news = (TextView) view.findViewById(R.id.news);
            date = (TextView) view.findViewById(R.id.date);
            link = (LinearLayout) view.findViewById(R.id.link);
            divider = (LinearLayout) view.findViewById(R.id.divider);
        }
    }

}