package amu.roboclub.news;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.news)
    TextView newsText;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.link)
    LinearLayout link;
    @BindView(R.id.divider)
    LinearLayout divider;

    public NewsHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }

    public void setNews(Context context, News news) {
        newsText.setText(news.notice);
        date.setText(news.date);

        if (news.link == null) {
            link.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            return;
        }

        link.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        link.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(news.link));
            try {
                context.startActivity(i);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(link, R.string.app_not_found, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
